package worker;

import model.PizzaOrder;
import storage.OrderQueue;
import storage.SynchronizedWarehouse;


public class Baker implements Worker {
    private final int id;
    private final long cookingTime;
    private final OrderQueue orderQueue;
    private final SynchronizedWarehouse warehouse;
    private volatile boolean working = true;

    public Baker(int id, long cookingTime, OrderQueue orderQueue, SynchronizedWarehouse warehouse) {
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
            // Получить заказ из очереди
            PizzaOrder order = orderQueue.dequeue();

            if (order == null) {
                if (!working) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            order.setState(PizzaOrder.OrderState.COOKING);
            order.printStatus();

            try {
                Thread.sleep(cookingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            order.setState(PizzaOrder.OrderState.WAITING_FOR_STORAGE);
            order.printStatus();

            while (!warehouse.reserveSpace() && working) {
                try {
                    warehouse.waitForSpace(500);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (!working) {
                warehouse.releaseReservation();
                break;
            }

            boolean added = warehouse.addPizza(order);
            if (added) {
                warehouse.releaseReservation();
            } else {
                warehouse.releaseReservation();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
