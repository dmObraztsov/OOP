package core.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static synchronized void info(String message) {
        log("INFO", message);
    }

    public static synchronized void debug(String message) {
        log("DEBUG", message);
    }

    public static synchronized void error(String message) {
        System.err.println(format("ERROR", message));
    }

    private static void log(String level, String message) {
        System.out.println(format(level, message));
    }

    private static String format(String level, String message) {
        return String.format("[%s] [%s] [%s] %s",
                LocalTime.now().format(formatter), level, Thread.currentThread().getName(), message);
    }
}