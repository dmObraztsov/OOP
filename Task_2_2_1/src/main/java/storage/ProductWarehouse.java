package storage;

import model.PizzaOrder;

public interface ProductWarehouse {

    boolean addPizza(PizzaOrder order);

    boolean reserveSpace();

    void releaseReservation();

    PizzaOrder[] takePizzas(int count);

    int getAvailableCount();

    int getDeliveredCount();

    boolean isFull();

    boolean isEmpty();

    int getReservationCount();
}
