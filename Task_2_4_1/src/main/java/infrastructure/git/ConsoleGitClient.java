package infrastructure.git;

import core.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConsoleGitClient implements GitClient {

    private int executeCommand(Path workingDir, String... command) {
        try {
            if (!Files.exists(workingDir)) {
                Files.createDirectories(workingDir);
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir.toFile());

            // Убираем inheritIO(), чтобы ошибки не летели в консоль вперемешку с логами,
            // а обрабатывались нами через Logger.
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                Logger.error("Команда завершилась с ошибкой " + exitCode + ": " + String.join(" ", command));
            }

            return exitCode;
        } catch (IOException | InterruptedException e) {
            Logger.error("Критический сбой при выполнении: " + String.join(" ", command) + " | " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cloneRepository(String url, Path destination) {
        Logger.info("Клонирование репозитория: " + url + " -> " + destination.getFileName());
        // Выполняем из корня временной папки
        executeCommand(destination.getParent(), "git", "clone", url, destination.toAbsolutePath().toString());
    }

    @Override
    public void checkoutBranch(Path repoPath, String branchName) {
        try {
            Logger.debug("[" + repoPath.getFileName() + "] Очистка рабочей копии...");
            executeCommand(repoPath, "git", "reset", "--hard", "HEAD");
            executeCommand(repoPath, "git", "clean", "-fd");

            Logger.info("[" + repoPath.getFileName() + "] Переключение на ветку: " + branchName);
            int exitCode = executeCommand(repoPath, "git", "checkout", "-f", branchName, "--");

            if (exitCode != 0) {
                Logger.debug("[" + repoPath.getFileName() + "] Ветка локально не найдена, пробуем fetch origin...");
                executeCommand(repoPath, "git", "fetch", "origin");
                int secondAttempt = executeCommand(repoPath, "git", "checkout", "-f", "-b", branchName, "origin/" + branchName, "--");

                if (secondAttempt != 0) {
                    Logger.error("[" + repoPath.getFileName() + "] Не удалось найти ветку: " + branchName);
                }
            }
        } catch (Exception e) {
            Logger.error("[" + repoPath.getFileName() + "] Ошибка Git checkout: " + e.getMessage());
        }
    }

    @Override
    public void fetchAll(Path repoPath) {
        Logger.debug("[" + repoPath.getFileName() + "] Обновление данных (git fetch --all)...");
        executeCommand(repoPath, "git", "fetch", "--all");
    }

    @Override
    public boolean testConnection(String url) {
        try {
            Logger.debug("Проверка соединения с " + url);
            ProcessBuilder pb = new ProcessBuilder("git", "ls-remote", "-h", url);
            pb.environment().put("GIT_TERMINAL_PROMPT", "0");

            Process process = pb.start();
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception e) {
            Logger.error("Ошибка при проверке соединения: " + e.getMessage());
            return false;
        }
    }

    public LocalDate getFirstCommitDate(Path repoPath, String branchName) {
        try {
            String out = executeAndGetOutput(repoPath, "git", "log", "--reverse", "--format=%as");
            if (!out.isEmpty()) {
                return LocalDate.parse(out.split("\n")[0]);
            }
        } catch (Exception e) {
            Logger.error("[" + repoPath.getFileName() + "] Ошибка даты первого коммита: " + e.getMessage());
        }
        return LocalDate.now();
    }

    public LocalDate getLastCommitDate(Path repoPath, String branchName) {
        try {
            String out = executeAndGetOutput(repoPath, "git", "log", "-1", "--format=%as");
            if (!out.isEmpty()) {
                return LocalDate.parse(out.trim());
            }
        } catch (Exception e) {
            Logger.error("[" + repoPath.getFileName() + "] Ошибка даты последнего коммита: " + e.getMessage());
        }
        return LocalDate.now();
    }

    private String executeAndGetOutput(Path workingDir, String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDir.toFile())
                    .start();
            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (Exception e) {
            Logger.error("Ошибка получения вывода команды: " + String.join(" ", command));
            return "";
        }
    }

    public List<String> getCommitDates(Path repoPath) {
        Logger.debug("[" + repoPath.getFileName() + "] Сбор дат всех коммитов для активности...");
        return executeAndGetLines(repoPath, "git", "log", "--all", "--no-merges", "--format=%aI");
    }

    private List<String> executeAndGetLines(Path path, String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(path.toFile())
                    .start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.lines().toList();
            }
        } catch (Exception e) {
            return List.of();
        }
    }
}