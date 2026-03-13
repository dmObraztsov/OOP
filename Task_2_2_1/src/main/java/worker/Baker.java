package worker;

import logger.Logger;
import model.PizzaOrder;
import storage.OrderQueue;
import storage.ProductWarehouse;

public class Baker implements Worker {
    private final int id;
    private final long cookingTime;
    private final OrderQueue orderQueue;
    private final ProductWarehouse warehouse;
    private volatile boolean working = true;

    public Baker(int id, long cookingTime, OrderQueue orderQueue, ProductWarehouse warehouse) {
        this.id = id;
        this.cookingTime = cookingTime;
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Role getRole() {
        return Role.BAKER;
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
        while (working || !orderQueue.isEmpty()) {
            try {
                PizzaOrder order = orderQueue.dequeue(250);
                if (order == null) {
                    continue;
                }
                order.setState(PizzaOrder.OrderState.COOKING);
                Logger.logOrder(order);
                Thread.sleep(cookingTime);
                warehouse.addPizza(order);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
