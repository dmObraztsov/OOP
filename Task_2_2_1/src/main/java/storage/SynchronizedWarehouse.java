package storage;

import model.PizzaOrder;
import java.util.*;

public class SynchronizedWarehouse implements ProductWarehouse {
    private final int capacity;
    private final List<PizzaOrder> storage;
    private final Object lock = new Object();
    private int reservations = 0;
    private int deliveredCount = 0;

    public SynchronizedWarehouse(int capacity) {
        this.capacity = capacity;
        this.storage = new ArrayList<>();
    }

    @Override
    public boolean addPizza(PizzaOrder order) {
        synchronized (lock) {
            if (storage.size() >= capacity) {
                return false;
            }
            storage.add(order);
            order.setState(PizzaOrder.OrderState.IN_STORAGE);
            order.printStatus();
            lock.notifyAll();
            return true;
        }
    }

    @Override
    public boolean reserveSpace() {
        synchronized (lock) {
            if (storage.size() + reservations >= capacity) {
                return false;
            }
            reservations++;
            return true;
        }
    }

    @Override
    public void releaseReservation() {
        synchronized (lock) {
            if (reservations > 0) {
                reservations--;
                lock.notifyAll();
            }
        }
    }

    @Override
    public PizzaOrder[] takePizzas(int count) {
        synchronized (lock) {
            int available = Math.min(count, storage.size());
            if (available == 0) {
                return new PizzaOrder[0];
            }

            PizzaOrder[] pizzas = new PizzaOrder[available];
            for (int i = 0; i < available; i++) {
                pizzas[i] = storage.remove(0);
            }

            lock.notifyAll();
            return pizzas;
        }
    }

    @Override
    public int getAvailableCount() {
        synchronized (lock) {
            return storage.size();
        }
    }

    @Override
    public int getDeliveredCount() {
        return deliveredCount;
    }

    @Override
    public boolean isFull() {
        synchronized (lock) {
            return storage.size() + reservations >= capacity;
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return storage.isEmpty();
        }
    }

    @Override
    public int getReservationCount() {
        synchronized (lock) {
            return reservations;
        }
    }


    public void markDelivered(int count) {
        deliveredCount += count;
    }

    public void waitForPizzas(long timeout) {
        synchronized (lock) {
            while (storage.isEmpty()) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void waitForSpace(long timeout) {
        synchronized (lock) {
            while (storage.size() + reservations >= capacity) {
                try {
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
