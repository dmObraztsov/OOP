import model.PizzaOrder;
import org.junit.jupiter.api.Test;
import storage.SynchronizedWarehouse;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedWarehouseTest {

    @Test
    void reserveSpaceAndFullLogic() {
        SynchronizedWarehouse w = new SynchronizedWarehouse(2);
        assertTrue(w.isEmpty());
        assertFalse(w.isFull());
        assertEquals(0, w.getReservationCount());

        assertTrue(w.reserveSpace());
        assertEquals(1, w.getReservationCount());
        assertTrue(w.reserveSpace());
        assertEquals(2, w.getReservationCount());
        assertTrue(w.isFull(), "reservations should count towards fullness");

        assertFalse(w.reserveSpace(), "should not reserve when full");

        w.releaseReservation();
        assertEquals(1, w.getReservationCount());
        assertFalse(w.isFull());
    }

    @Test
    void addPizzaUpdatesStateAndCounts() {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        PizzaOrder o = new PizzaOrder(1);
        o.setState(PizzaOrder.OrderState.WAITING_FOR_STORAGE);

        assertTrue(w.addPizza(o));
        assertEquals(1, w.getAvailableCount());
        assertEquals(PizzaOrder.OrderState.IN_STORAGE, o.getState());
        assertTrue(w.isFull());

        PizzaOrder o2 = new PizzaOrder(2);
        assertFalse(w.addPizza(o2), "should not add when capacity reached");
    }

    @Test
    void takePizzasRemovesFromStorage() {
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
    void markDeliveredIncrementsCounter() {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        assertEquals(0, w.getDeliveredCount());
        w.markDelivered(3);
        assertEquals(3, w.getDeliveredCount());
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
    void waitForSpaceUnblocksWhenPizzaTaken() throws Exception {
        SynchronizedWarehouse w = new SynchronizedWarehouse(1);
        w.addPizza(new PizzaOrder(1));
        assertTrue(w.isFull());

        long[] elapsed = new long[1];
        Thread waiter = new Thread(() -> {
            long start = System.currentTimeMillis();
            w.waitForSpace(5_000);
            elapsed[0] = System.currentTimeMillis() - start;
        });

        waiter.start();
        Thread.sleep(150);
        w.takePizzas(1);

        waiter.join(2_000);
        assertFalse(waiter.isAlive());
        assertTrue(elapsed[0] < 2_000);
    }
}
