import core.app.GradingApplication;
import core.model.CourseConfiguration;
import dsl.ConfigurationLoader;
import core.util.Logger;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8.name()));

            Logger.info("Загрузка конфигурации...");
            ConfigurationLoader loader = new ConfigurationLoader();
            CourseConfiguration config = loader.load(new File("grading.groovy"));

            Path workspacePath = Path.of(System.getProperty("user.home")).resolve("grading_temp");

            new GradingApplication().run(config, workspacePath);

        } catch (Exception e) {
            Logger.error("Критический сбой: " + e.getMessage());
            e.printStackTrace();
        }
    }
}