import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedWarehouse;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedWarehouseTest {

    @Test
    void addPizzaUpdatesStateAndCounts() throws Exception {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        PizzaOrder o = new PizzaOrder(1);
        o.setState(PizzaOrder.OrderState.WAITING_FOR_STORAGE);

        assertTrue(w.addPizza(o));
        assertEquals(1, w.getAvailableCount());
        assertEquals(PizzaOrder.OrderState.IN_STORAGE, o.getState());
        assertTrue(w.isFull());
        assertEquals(1, w.getReceivedCount());
    }

    @Test
    void takePizzasRemovesFromStorage() throws Exception {
        SynchronizedWarehouse w = new SynchronizedWarehouse(5);
        PizzaOrder a = new PizzaOrder(1);
        PizzaOrder b = new PizzaOrder(2);
        PizzaOrder c = new PizzaOrder(3);
        w.addPizza(a);
        w.addPizza(b);
        w.addPizza(c);

        PizzaOrder[] taken = w.takePizzas(2);
        assertEquals(2, taken.length);
        assertEquals(1, w.getAvailableCount());

        PizzaOrder[] taken2 = w.takePizzas(10);
        assertEquals(1, taken2.length);
        assertEquals(0, w.getAvailableCount());
        assertTrue(w.isEmpty());

        PizzaOrder[] taken3 = w.takePizzas(1);
        assertEquals(0, taken3.length);
    }

    @Test
    void incrementDeliveredIncrementsCounter() {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        assertEquals(0, w.getDeliveredCount());
        w.incrementDelivered();
        w.incrementDelivered();
        assertEquals(2, w.getDeliveredCount());
    }

    @Test
    void waitForPizzasUnblocksWhenPizzaAdded() throws Exception {
        SynchronizedWarehouse w = new SynchronizedWarehouse(2);

        long[] elapsed = new long[1];
        Thread waiter = new Thread(() -> {
            long start = System.currentTimeMillis();
            w.waitForPizzas(5_000);
            elapsed[0] = System.currentTimeMillis() - start;
        });

        waiter.start();
        Thread.sleep(150);
        w.addPizza(new PizzaOrder(1));

        waiter.join(2_000);
        assertFalse(waiter.isAlive());
        assertTrue(elapsed[0] < 2_000);
    }

    @Test
    void addPizzaBlocksWhenFullAndUnblocksAfterTake() throws Exception {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        w.addPizza(new PizzaOrder(1));

        PizzaOrder delayed = new PizzaOrder(2);
        Thread adder = new Thread(() -> {
            try {
                w.addPizza(delayed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        adder.start();
        Thread.sleep(150);
        assertTrue(adder.isAlive(), "должен ждать место на складе");

        PizzaOrder[] taken = w.takePizzas(1);
        assertEquals(1, taken.length);

        adder.join(2_000);
        assertFalse(adder.isAlive(), "после освобождения места поток должен продолжить");
        assertEquals(1, w.getAvailableCount());
    }
}
