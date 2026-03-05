package worker;

import model.PizzaOrder;
import storage.SynchronizedWarehouse;

public class Courier implements Worker {
    private final int id;
    private final int bagCapacity;
    private final long deliveryTime;
    private final SynchronizedWarehouse warehouse;
    private volatile boolean working = true;

    public Courier(int id, int bagCapacity, long deliveryTime, SynchronizedWarehouse warehouse) {
        this.id = id;
        this.bagCapacity = bagCapacity;
        this.deliveryTime = deliveryTime;
        this.warehouse = warehouse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isWorking() {
        return working;
    }

    @Override
    public void stop() {
        working = false;
    }

    @Override
    public void run() {
        while (working) {
            if (warehouse.isEmpty()) {
                try {
                    warehouse.waitForPizzas(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            PizzaOrder[] pizzas = warehouse.takePizzas(bagCapacity);

            if (pizzas.length == 0) {
                if (!working) {
                    break;
                }
                continue;
            }

            for (PizzaOrder pizza : pizzas) {
                pizza.setState(PizzaOrder.OrderState.IN_DELIVERY);
                pizza.printStatus();
            }

            try {
                Thread.sleep(deliveryTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            for (PizzaOrder pizza : pizzas) {
                pizza.setState(PizzaOrder.OrderState.DELIVERED);
                pizza.printStatus();
            }

            warehouse.markDelivered(pizzas.length);
        }
    }
}
