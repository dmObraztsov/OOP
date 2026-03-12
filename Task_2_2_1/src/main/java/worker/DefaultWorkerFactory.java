package worker;

import config.PizzaShopConfig;
import storage.OrderQueue;
import storage.ProductWarehouse;

import java.util.ArrayList;
import java.util.List;

public class DefaultWorkerFactory implements WorkerFactory {
    @Override
    public List<Worker> createWorkers(PizzaShopConfig config, OrderQueue orderQueue, ProductWarehouse warehouse) {
        List<Worker> workers = new ArrayList<>();

        for (PizzaShopConfig.BakerConfig bakerConfig : config.bakers) {
            workers.add(new Baker(bakerConfig.id, bakerConfig.cookingTime, orderQueue, warehouse));
        }

        for (PizzaShopConfig.CourierConfig courierConfig : config.couriers) {
            workers.add(new Courier(courierConfig.id, courierConfig.bagCapacity, courierConfig.deliveryTime, warehouse));
        }

        return workers;
    }
}
