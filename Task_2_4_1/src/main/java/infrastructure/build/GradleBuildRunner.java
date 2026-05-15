package infrastructure.build;

import core.util.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class GradleBuildRunner implements BuildRunner {

    @Override
    public BuildResult run(Path projectPath) {
        return runCompile(projectPath).compileSuccess()
                ? new BuildResult(true, runCheckstyle(projectPath), 0, 0, 0)
                : new BuildResult(false, false, 0, 0, 0);
    }

    public BuildResult runCompile(Path projectPath) {
        String gradle = gradleExecutable(projectPath);
        if (gradle == null) return new BuildResult(false, false, 0, 0, 0);

        Logger.info("[" + projectPath.getFileName() + "] Компиляция...");
        int exit = execute(projectPath, gradle, "-Dfile.encoding=UTF-8", "clean", "compileJava");

        if (exit != 0) {
            Logger.error("[" + projectPath.getFileName() + "] Сбой компиляции Java.");
        }
        return new BuildResult(exit == 0, false, 0, 0, 0);
    }

    public boolean runCheckstyle(Path projectPath) {
        Path buildGradle = projectPath.resolve("build.gradle");
        if (!Files.exists(buildGradle)) {
            Logger.debug("[" + projectPath.getFileName() + "] Checkstyle: build.gradle не найден.");
            return true;
        }

        try {
            String content = Files.readString(buildGradle);
            if (!content.contains("checkstyle")) {
                Logger.debug("[" + projectPath.getFileName() + "] Checkstyle: плагин не настроен.");
                return true;
            }
        } catch (Exception e) {
            return true;
        }

        String gradle = gradleExecutable(projectPath);
        if (gradle == null) return false;

        Logger.debug("[" + projectPath.getFileName() + "] Запуск Checkstyle...");
        execute(projectPath, gradle, "-Dfile.encoding=UTF-8", "checkstyleMain", "--continue");

        Path reportPath = projectPath.resolve("build/reports/checkstyle/main.xml");
        if (!Files.exists(reportPath)) {
            return true;
        }

        try {
            String xmlContent = Files.readString(reportPath);
            boolean hasErrors = xmlContent.contains("<error");
            if (hasErrors) {
                Logger.debug("[" + projectPath.getFileName() + "] Checkstyle: FAIL");
                return false;
            }
            Logger.debug("[" + projectPath.getFileName() + "] Checkstyle: OK");
            return true;
        } catch (Exception e) {
            Logger.error("[" + projectPath.getFileName() + "] Ошибка парсинга Checkstyle XML.");
            return true;
        }
    }

    public boolean runTests(Path projectPath) {
        String gradle = gradleExecutable(projectPath);
        if (gradle == null) return false;

        Logger.info("[" + projectPath.getFileName() + "] Запуск тестов и JaCoCo...");
        int exit = execute(projectPath, gradle, "-Dfile.encoding=UTF-8", "test", "jacocoTestReport");

        if (exit != 0) {
            Logger.debug("[" + projectPath.getFileName() + "] Некоторые тесты упали (это нормально для отчета).");
        }
        return exit != -1;
    }

    private String gradleExecutable(Path projectPath) {
        String name = System.getProperty("os.name").toLowerCase().contains("win")
                ? "gradlew.bat" : "./gradlew";
        Path path = projectPath.resolve(name).toAbsolutePath();
        if (!Files.exists(path)) {
            Logger.error("[" + projectPath.getFileName() + "] Gradle Wrapper не найден!");
            return null;
        }
        return path.toString();
    }

    private int execute(Path projectPath, String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(projectPath.toFile())
                    .redirectErrorStream(true)
                    .start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                     if (line.contains("FAILED")) Logger.debug("[" + projectPath.getFileName() + "] " + line);
                }
            }

            return process.waitFor();
        } catch (Exception e) {
            Logger.error("[" + projectPath.getFileName() + "] Ошибка выполнения Gradle: " + e.getMessage());
            return -1;
        }
    }
}