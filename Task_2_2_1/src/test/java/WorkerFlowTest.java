import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedOrderQueue;
import storage.SynchronizedWarehouse;
import worker.Baker;
import worker.Courier;

import static org.junit.jupiter.api.Assertions.*;

public class WorkerFlowTest {

    @Test
    void bakerAndCourierDeliverOrdersEndToEnd() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        SynchronizedWarehouse w = new SynchronizedWarehouse(2);

        PizzaOrder o1 = new PizzaOrder(1);
        q.enqueue(o1);

        Baker baker = new Baker(1, 10, q, w);
        Courier courier = new Courier(1, 1, 10, w);

        Thread bt = new Thread(baker);
        Thread ct = new Thread(courier);
        bt.start();
        ct.start();

        long timeout = System.currentTimeMillis() + 3000;
        while (o1.getState() != PizzaOrder.OrderState.DELIVERED && System.currentTimeMillis() < timeout) {
            Thread.sleep(50);
        }

        baker.stop();
        courier.stop();
        bt.join(1000);
        ct.join(1000);

        assertEquals(PizzaOrder.OrderState.DELIVERED, o1.getState());
        assertTrue(q.isEmpty());
    }
}
