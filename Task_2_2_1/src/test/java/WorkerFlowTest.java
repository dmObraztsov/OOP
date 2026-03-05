import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedOrderQueue;
import storage.SynchronizedWarehouse;
import worker.Baker;
import worker.Courier;

import static org.junit.jupiter.api.Assertions.*;

public class WorkerFlowTest {

    @Test
    void bakerAndCourierDeliverOrders_endToEnd() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);

        PizzaOrder o1 = new PizzaOrder(1);
        PizzaOrder o2 = new PizzaOrder(2);
        q.enqueue(o1);
        q.enqueue(o2);

        Baker baker = new Baker(1, 10, q, w);
        Courier courier = new Courier(1, 1, 50, w);

        Thread bt = new Thread(baker, "BakerTest");
        Thread ct = new Thread(courier, "CourierTest");
        bt.start();
        ct.start();

        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            if (o1.getState() == PizzaOrder.OrderState.DELIVERED &&
                o2.getState() == PizzaOrder.OrderState.DELIVERED) {
                break;
            }
            Thread.sleep(20);
        }

        baker.stop();
        courier.stop();
        bt.interrupt();
        ct.interrupt();
        bt.join(2_000);
        ct.join(2_000);

        assertEquals(PizzaOrder.OrderState.DELIVERED, o1.getState());
        assertEquals(PizzaOrder.OrderState.DELIVERED, o2.getState());
        assertTrue(w.getDeliveredCount() >= 2, "delivered counter should be updated");
        assertTrue(q.isEmpty());
    }

    @Test
    void workersStopWhenInterruptedWithNoWork() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);

        Baker baker = new Baker(1, 1_000, q, w);
        Courier courier = new Courier(1, 1, 1_000, w);

        Thread bt = new Thread(baker);
        Thread ct = new Thread(courier);
        bt.start();
        ct.start();

        Thread.sleep(100);
        baker.stop();
        courier.stop();
        bt.interrupt();
        ct.interrupt();

        bt.join(2_000);
        ct.join(2_000);
        assertFalse(bt.isAlive());
        assertFalse(ct.isAlive());
        assertFalse(baker.isWorking());
        assertFalse(courier.isWorking());
    }
}
