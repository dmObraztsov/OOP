package logger;

import model.PizzaOrder;

public class Logger {
    public static void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void logOrder(PizzaOrder order) {
        System.out.println(order);
    }
}
