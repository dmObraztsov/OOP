package storage;

import model.PizzaOrder;
import java.util.*;

public class SynchronizedOrderQueue implements OrderQueue {
    private final Queue<PizzaOrder> queue;
    private final Object lock = new Object();
    private int processedCount = 0;

    public SynchronizedOrderQueue() {
        this.queue = new LinkedList<>();
    }

    @Override
    public void enqueue(PizzaOrder order) {
        synchronized (lock) {
            queue.add(order);
            lock.notifyAll();
        }
    }

    @Override
    public PizzaOrder dequeue() {
        synchronized (lock) {
            if (queue.isEmpty()) {
                return null;
            }
            PizzaOrder order = queue.poll();
            if (order != null) {
                processedCount++;
            }
            return order;
        }
    }

    @Override
    public int size() {
        synchronized (lock) {
            return queue.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (lock) {
            return queue.isEmpty();
        }
    }

    @Override
    public int getProcessedCount() {
        synchronized (lock) {
            return processedCount;
        }
    }

    public void waitForOrder(long timeout) {
        synchronized (lock) {
            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void notifyNewOrder() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
