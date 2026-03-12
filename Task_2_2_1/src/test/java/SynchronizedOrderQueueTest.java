import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedOrderQueue;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedOrderQueueTest {

    @Test
    void enqueueDequeueUpdatesSizeAndProcessedCount() throws Exception {
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

        PizzaOrder d1 = q.dequeue(50);
        assertNotNull(d1);
        PizzaOrder d2 = q.dequeue(50);
        assertNotNull(d2);
        assertNull(q.dequeue(50));

        assertEquals(0, q.size());
        assertTrue(q.isEmpty());
        assertEquals(2, q.getProcessedCount());
    }

    @Test
    void dequeueUnblocksOnEnqueue() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();

        PizzaOrder[] result = new PizzaOrder[1];
        Thread waiter = new Thread(() -> {
            try {
                result[0] = q.dequeue(5_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        waiter.start();
        Thread.sleep(150);
        q.enqueue(new PizzaOrder(1));

        waiter.join(2_000);
        assertFalse(waiter.isAlive(), "waiter thread should have unblocked");
        assertNotNull(result[0]);
        assertEquals(1, result[0].getOrderId());
    }

    @Test
    void dequeueTimeoutReturnsNull() throws Exception {
        SynchronizedOrderQueue q = new SynchronizedOrderQueue();
        long start = System.currentTimeMillis();
        PizzaOrder order = q.dequeue(200);
        long elapsed = System.currentTimeMillis() - start;

        assertNull(order);
        assertTrue(elapsed >= 150, "dequeue должен ждать таймаут");
    }
}
