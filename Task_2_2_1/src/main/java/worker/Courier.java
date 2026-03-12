package worker;

import logger.Logger;
import model.PizzaOrder;
import storage.ProductWarehouse;

import java.util.concurrent.ThreadLocalRandom;

public class Courier implements Worker {
    private final int id;
    private final int bagCapacity;
    private final long maxDeliveryTime;
    private final ProductWarehouse warehouse;
    private volatile boolean working = true;

    public Courier(int id, int bagCapacity, long deliveryTime, ProductWarehouse warehouse) {
        this.id = id;
        this.bagCapacity = bagCapacity;
        this.maxDeliveryTime = deliveryTime;
        this.warehouse = warehouse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Role getRole() {
        return Role.COURIER;
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
        while (working || !warehouse.isEmpty()) {
            warehouse.waitForPizzas(250);
            PizzaOrder[] orders = warehouse.takePizzas(bagCapacity);
            if (orders.length == 0) {
                continue;
            }
            try {
                for (PizzaOrder order : orders) {
                    order.setState(PizzaOrder.OrderState.IN_DELIVERY);
                    Logger.logOrder(order);
                }
                Thread.sleep(randomDeliveryTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                for (PizzaOrder order : orders) {
                    order.setState(PizzaOrder.OrderState.DELIVERED);
                    warehouse.incrementDelivered();
                    Logger.logOrder(order);
                }
            }
        }
    }

    private long randomDeliveryTime() {
        if (maxDeliveryTime <= 1) {
            return Math.max(maxDeliveryTime, 0);
        }
        return ThreadLocalRandom.current().nextLong(1, maxDeliveryTime + 1);
    }
}
