import config.PizzaShopConfig;
import model.PizzaOrder;
import storage.SynchronizedOrderQueue;
import storage.SynchronizedWarehouse;
import worker.Baker;
import worker.Courier;
import worker.Worker;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PizzaShop {
    private final PizzaShopConfig config;
    private final SynchronizedOrderQueue orderQueue;
    private final SynchronizedWarehouse warehouse;
    private final List<Thread> workerThreads;
    private final List<Worker> workers;
    private final List<PizzaOrder> unfinishedOrders;
    private final AtomicInteger orderCounter;
    private volatile boolean isAcceptingOrders = true;
    private Thread orderGeneratorThread;

    public PizzaShop(PizzaShopConfig config) {
        this.config = config;
        this.orderQueue = new SynchronizedOrderQueue();
        this.warehouse = new SynchronizedWarehouse(config.warehouseCapacity);
        this.workerThreads = new ArrayList<>();
        this.workers = new ArrayList<>();
        this.unfinishedOrders = Collections.synchronizedList(new ArrayList<>());
        this.orderCounter = new AtomicInteger(0);
    }

    public void start() {
        System.out.println("=== ПИЦЦЕРИЯ ОТКРЫВАЕТСЯ ===");
        System.out.println("Пекарей: " + config.bakers.size());
        System.out.println("Курьеров: " + config.couriers.size());
        System.out.println("Вместимость склада: " + config.warehouseCapacity);
        System.out.println("Время работы: " + config.workingTime + " мс");
        System.out.println("=====================================\n");

        for (PizzaShopConfig.BakerConfig bakerConfig : config.bakers) {
            Baker baker = new Baker(bakerConfig.id, bakerConfig.cookingTime, orderQueue, warehouse);
            workers.add(baker);
            Thread thread = new Thread(baker);
            thread.setName("Baker-" + bakerConfig.id);
            thread.start();
            workerThreads.add(thread);
        }

        for (PizzaShopConfig.CourierConfig courierConfig : config.couriers) {
            Courier courier = new Courier(courierConfig.id, courierConfig.bagCapacity,
                    courierConfig.deliveryTime, warehouse);
            workers.add(courier);
            Thread thread = new Thread(courier);
            thread.setName("Courier-" + courierConfig.id);
            thread.start();
            workerThreads.add(thread);
        }

        orderGeneratorThread = new Thread(this::generateOrders);
        orderGeneratorThread.setName("OrderGenerator");
        orderGeneratorThread.start();
        workerThreads.add(orderGeneratorThread);

        try {
            Thread.sleep(config.workingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Graceful shutdown: прекращаем прием заказов и завершаем оставшиеся
        shutdownGracefully();
    }

    private void generateOrders() {
        long delayBetweenOrders = 1000 / config.ordersPerSecond;
        while (isAcceptingOrders) {
            try {
                int orderId = orderCounter.incrementAndGet();
                PizzaOrder order = new PizzaOrder(orderId);
                unfinishedOrders.add(order);
                orderQueue.enqueue(order);
                order.printStatus();
                Thread.sleep(delayBetweenOrders);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdownGracefully() {
        System.out.println("\n=== ПИЦЦЕРИЯ ЗАКРЫВАЕТСЯ ===");
        isAcceptingOrders = false;

        if (orderGeneratorThread != null) {
            orderGeneratorThread.interrupt();
        }


        System.out.println("Останавливаем прием новых заказов. Дорабатываем оставшиеся...");

        long startTime = System.currentTimeMillis();
        long maxWaitTimeMs = 30_000; // защитный таймаут, чтобы не зависнуть навсегда

        while (System.currentTimeMillis() - startTime < maxWaitTimeMs) {
            if (orderQueue.isEmpty() && warehouse.isEmpty() && allOrdersDelivered()) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        stopWorkersAndJoin();

        saveUnfinishedOrdersSnapshot(snapshotUnfinishedOrders());
        printStatistics();
        System.out.println("\n=== ПИЦЦЕРИЯ ЗАКРЫТА ===");
    }

    private void stopWorkersAndJoin() {
        System.out.println("\nОстанавливаем рабочих...");
        for (Worker worker : workers) {
            worker.stop();
        }

        for (Thread t : workerThreads) {
            t.interrupt();
        }

        for (Thread t : workerThreads) {
            try {
                t.join(5_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean allOrdersDelivered() {
        for (PizzaOrder order : unfinishedOrders) {
            if (order.getState() != PizzaOrder.OrderState.DELIVERED) {
                return false;
            }
        }
        return true;
    }

    private List<PizzaOrder> snapshotUnfinishedOrders() {
        List<PizzaOrder> snapshot = new ArrayList<>();
        synchronized (unfinishedOrders) {
            for (PizzaOrder order : unfinishedOrders) {
                if (order.getState() != PizzaOrder.OrderState.DELIVERED) {
                    PizzaOrder copy = new PizzaOrder(order.getOrderId());
                    copy.setState(order.getState());
                    snapshot.add(copy);
                }
            }
        }
        return snapshot;
    }

    private void saveUnfinishedOrdersSnapshot(List<PizzaOrder> unfinished) {
        if (unfinished.isEmpty()) {
            System.out.println("\nВсе заказы завершены успешно!");
            return;
        }

        System.out.println("\nНезавершенные заказы (" + unfinished.size() + "):");

        File outFile = new File("src/main/resources/unfinished_orders.txt");
        File parent = outFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8))) {
            for (PizzaOrder order : unfinished) {
                System.out.println("[Заказ " + order.getOrderId() + "] " + order.getState().getDescription());
                writer.println("Order: " + order.getOrderId() + ", State: " + order.getState());
            }
            System.out.println("\nНезавершенные заказы сохранены в resources/unfinished_orders.txt");
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении незавершенных заказов: " + e.getMessage());
        }
    }

    private void printStatistics() {
        System.out.println("\n=== СТАТИСТИКА РАБОТЫ ===");
        System.out.println("Всего заказов создано: " + orderCounter.get());
        System.out.println("Доставлено пицц: " + warehouse.getDeliveredCount());
        System.out.println("На складе осталось пицц: " + warehouse.getAvailableCount());

        int pending = 0;
        int cooking = 0;
        int inStorage = 0;
        int inDelivery = 0;
        int delivered = 0;

        for (PizzaOrder order : unfinishedOrders) {
            switch (order.getState()) {
                case PENDING:
                case WAITING_FOR_STORAGE:
                    pending++;
                    break;
                case COOKING:
                    cooking++;
                    break;
                case IN_STORAGE:
                    inStorage++;
                    break;
                case IN_DELIVERY:
                    inDelivery++;
                    break;
                case DELIVERED:
                    delivered++;
                    break;
            }
        }

        System.out.println("\nРаспределение заказов по состояниям:");
        System.out.println("  В очереди/ожидании: " + pending);
        System.out.println("  Готовятся: " + cooking);
        System.out.println("  На складе: " + inStorage);
        System.out.println("  На доставке: " + inDelivery);
        System.out.println("  Доставлены: " + delivered);
        System.out.println("========================");
    }

    public static void main(String[] args) {
        try {
            PizzaShopConfig config = PizzaShopConfig.loadFromJson("src/main/resources/config.json");

            PizzaShop pizzaShop = new PizzaShop(config);
            pizzaShop.start();
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
