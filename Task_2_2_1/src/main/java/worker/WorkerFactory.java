package worker;

import config.PizzaShopConfig;
import storage.OrderQueue;
import storage.ProductWarehouse;

import java.util.List;

public interface WorkerFactory {
    List<Worker> createWorkers(PizzaShopConfig config, OrderQueue orderQueue, ProductWarehouse warehouse);
}
