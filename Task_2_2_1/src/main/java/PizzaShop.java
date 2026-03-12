import config.PizzaShopConfig;
import logger.Logger;
import model.PizzaOrder;
import storage.OrderQueue;
import storage.ProductWarehouse;
import storage.SynchronizedOrderQueue;
import storage.SynchronizedWarehouse;
import worker.DefaultWorkerFactory;
import worker.Worker;
import worker.WorkerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PizzaShop {
    private final PizzaShopConfig config;
    private final OrderQueue orderQueue;
    private final ProductWarehouse warehouse;
    private final WorkerFactory workerFactory;

    private final List<Thread> workerThreads = new ArrayList<>();
    private final List<Worker> workers = new ArrayList<>();
    private final AtomicInteger orderCounter = new AtomicInteger(0);
    private final List<PizzaOrder> allOrders = new ArrayList<>();

    public PizzaShop(PizzaShopConfig config) {
        this(
                config,
                new SynchronizedOrderQueue(),
                new SynchronizedWarehouse(config.warehouseCapacity),
                new DefaultWorkerFactory()
        );
    }

    public PizzaShop(PizzaShopConfig config, OrderQueue orderQueue, ProductWarehouse warehouse, WorkerFactory workerFactory) {
        this.config = config;
        this.orderQueue = orderQueue;
        this.warehouse = warehouse;
        this.workerFactory = workerFactory;
    }

    public void start() {
        Logger.info("Пиццерия начинает работу...");
        workers.addAll(workerFactory.createWorkers(config, orderQueue, warehouse));

        for (Worker worker : workers) {
            Thread thread = new Thread(worker);
            workerThreads.add(thread);
            thread.start();
        }

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < config.workingTime) {
            generateOrder();
            try {
                Thread.sleep(1000L / config.ordersPerSecond);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        shutdown();
    }

    private void generateOrder() {
        PizzaOrder order = new PizzaOrder(orderCounter.incrementAndGet());
        synchronized (allOrders) {
            allOrders.add(order);
        }
        orderQueue.enqueue(order);
        Logger.logOrder(order);
    }

    private void shutdown() {
        Logger.info("Время работы истекло. Новые заказы больше не принимаются.");
        Logger.info("Режим завершения: FINISH_ALL (доставляем все созданные заказы)");

        stopByRole(Worker.Role.BAKER);
        waitUntil(() -> warehouse.getReceivedCount() >= allOrders.size());

        stopByRole(Worker.Role.COURIER);
        waitUntil(() -> getActuallyDeliveredCount() >= allOrders.size());

        stopAllThreadsAndJoin();

        Logger.info("=== ИТОГО ===");
        Logger.info("Заказов реально доставлено: " + getActuallyDeliveredCount());
        Logger.info("Всего заказов в системе: " + allOrders.size());
        Logger.info("=== ПИЦЦЕРИЯ ЗАКРЫТА ===");
    }

    private void stopByRole(Worker.Role role) {
        for (Worker worker : workers) {
            if (worker.getRole() == role) {
                worker.stop();
            }
        }
    }

    private void stopAllThreadsAndJoin() {
        for (Thread thread : workerThreads) {
            thread.interrupt();
        }
        for (Thread thread : workerThreads) {
            try {
                thread.join(1500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void waitUntil(Check check) {
        while (!check.done()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private long getActuallyDeliveredCount() {
        synchronized (allOrders) {
            return allOrders.stream().filter(order -> order.getState() == PizzaOrder.OrderState.DELIVERED).count();
        }
    }

    private interface Check {
        boolean done();
    }
}
