package storage;

import model.PizzaOrder;

public interface OrderQueue {
    void enqueue(PizzaOrder order);

    PizzaOrder dequeue();

    int size();

    boolean isEmpty();

    int getProcessedCount();
}
