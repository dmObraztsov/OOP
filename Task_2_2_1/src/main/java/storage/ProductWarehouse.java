package storage;

import model.PizzaOrder;

public interface ProductWarehouse {
    boolean addPizza(PizzaOrder order) throws InterruptedException;

    PizzaOrder[] takePizzas(int count);

    void waitForPizzas(long timeoutMs);

    void incrementDelivered();

    int getAvailableCount();

    int getDeliveredCount();

    int getReceivedCount();

    boolean isFull();

    boolean isEmpty();
}
