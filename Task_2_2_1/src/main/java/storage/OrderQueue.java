package storage;

import model.PizzaOrder;

public interface OrderQueue {
    void enqueue(PizzaOrder order);

    PizzaOrder dequeue(long timeoutMs) throws InterruptedException;

    int size();

    boolean isEmpty();

    int getProcessedCount();
}
