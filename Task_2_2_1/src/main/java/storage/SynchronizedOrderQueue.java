package storage;

import model.PizzaOrder;

import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedOrderQueue implements OrderQueue {
    private final Object lock = new Object();
    private final Queue<PizzaOrder> queue = new LinkedList<>();
    private int processedCount = 0;

    @Override
    public void enqueue(PizzaOrder order) {
        synchronized (lock) {
            queue.offer(order);
            lock.notifyAll();
        }
    }

    @Override
    public PizzaOrder dequeue(long timeoutMs) throws InterruptedException {
        synchronized (lock) {
            long deadline = System.currentTimeMillis() + timeoutMs;
            while (queue.isEmpty()) {
                long left = deadline - System.currentTimeMillis();
                if (left <= 0) {
                    return null;
                }
                lock.wait(left);
            }
            PizzaOrder order = queue.poll();
            processedCount++;
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
}
