package storage;

import logger.Logger;
import model.PizzaOrder;

import java.util.ArrayList;
import java.util.List;

public class SynchronizedWarehouse implements ProductWarehouse {
    private final Object lock = new Object();
    private final int capacity;
    private final List<PizzaOrder> storage = new ArrayList<>();
    private int deliveredCount = 0;
    private int receivedCount = 0;

    public SynchronizedWarehouse(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean addPizza(PizzaOrder order) throws InterruptedException {
        synchronized (lock) {
            while (storage.size() >= capacity) {
                order.setState(PizzaOrder.OrderState.WAITING_FOR_STORAGE);
                Logger.logOrder(order);
                lock.wait();
            }
            order.setState(PizzaOrder.OrderState.IN_STORAGE);
            storage.add(order);
            receivedCount++;
            Logger.logOrder(order);
            lock.notifyAll();
            return true;
        }
    }

    @Override
    public PizzaOrder[] takePizzas(int count) {
        synchronized (lock) {
            int toTake = Math.min(count, storage.size());
            PizzaOrder[] result = new PizzaOrder[toTake];
            for (int i = 0; i < toTake; i++) {
                result[i] = storage.remove(0);
            }
            if (toTake > 0) {
                lock.notifyAll();
            }
            return result;
        }
    }

    @Override
    public void waitForPizzas(long timeoutMs) {
        synchronized (lock) {
            if (!storage.isEmpty()) {
                return;
            }
            try {
                lock.wait(timeoutMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void incrementDelivered() {
        synchronized (lock) {
            deliveredCount++;
            lock.notifyAll();
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
        synchronized (lock) {
            return deliveredCount;
        }
    }

    @Override
    public int getReceivedCount() {
        synchronized (lock) {
            return receivedCount;
        }
    }

    @Override
    public boolean isFull() {
        synchronized (lock) {
            return storage.size() >= capacity;
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return storage.isEmpty();
        }
    }
}
