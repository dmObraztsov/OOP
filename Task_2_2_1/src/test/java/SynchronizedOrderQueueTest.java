import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedOrderQueue;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedOrderQueueTest {

    @Test
    void enqueueDequeueUpdatesSizeAndProcessedCount() {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        assertTrue(q.isEmpty());
        assertEquals(0, q.size());
        assertEquals(0, q.getProcessedCount());

        PizzaOrder o1 = new PizzaOrder(1);
        PizzaOrder o2 = new PizzaOrder(2);
        q.enqueue(o1);
        q.enqueue(o2);
        assertEquals(2, q.size());
        assertFalse(q.isEmpty());

        PizzaOrder d1 = q.dequeue();
        assertNotNull(d1);
        PizzaOrder d2 = q.dequeue();
        assertNotNull(d2);
        assertNull(q.dequeue());

        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
        assertEquals(2, q.getProcessedCount());
    }

    @Test
    void waitForOrderUnblocksOnEnqueue() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();

        long[] elapsed = new long[1];
        Thread waiter = new Thread(() -> {
            long start = System.currentTimeMillis();
            q.waitForOrder(5_000);
            elapsed[0] = System.currentTimeMillis() - start;
        });

        waiter.start();
        Thread.sleep(150);
        q.enqueue(new PizzaOrder(1));

        waiter.join(2_000);
        assertFalse(waiter.isAlive(), "waiter thread should have unblocked");
        assertTrue(elapsed[0] < 2_000, "waitForOrder should unblock shortly after enqueue");
    }

    @Test
    void notifyNewOrderDoesNotThrow() {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        assertDoesNotThrow(q::notifyNewOrder);
    }
}
