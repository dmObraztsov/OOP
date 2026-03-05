package worker;

import model.PizzaOrder;


public interface Worker extends Runnable {
    int getId();

    boolean isWorking();

    void stop();
}
