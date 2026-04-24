package infrastructure.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class ConsoleGitClient implements GitClient {

    private int executeCommand(Path workingDir, String... command) {
        try {
            if (!Files.exists(workingDir)) {
                Files.createDirectories(workingDir);
            }
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workingDir.toFile());
            pb.inheritIO();

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println("Command failed with code " + exitCode + ": " + String.join(" ", command));
            }

            return exitCode;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing command: " + String.join(" ", command), e);
        }
    }
    @Override
    public void cloneRepository(String url, Path destination) {
        String absoluteDestination = destination.toAbsolutePath().toString();
        System.out.println("Clone to: " + absoluteDestination);
        executeCommand(Path.of("."), "git", "clone", url, absoluteDestination);
    }

    @Override
    public void checkoutBranch(Path repoPath, String branchName) {
        try {
            executeCommand(repoPath, "git", "reset", "--hard", "HEAD");
            executeCommand(repoPath, "git", "clean", "-fd");

            System.out.println("[DEBUG] Switching to branch: " + branchName);

            int exitCode = executeCommand(repoPath, "git", "checkout", "-f", branchName, "--");

            if (exitCode != 0) {
                executeCommand(repoPath, "git", "fetch", "origin");
                executeCommand(repoPath, "git", "checkout", "-f", "-b", branchName, "origin/" + branchName, "--");
            }
        } catch (Exception e) {
            System.err.println("[DEBUG] [!] Git error: " + e.getMessage());
        }
    }

    @Override
    public boolean testConnection(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "ls-remote", url, "HEAD");
            Process process = pb.start();
            return process.waitFor(5, TimeUnit.SECONDS) && process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDate getSoftDeadlineDate(Path repoPath, String taskId) {
        String dateStr = executeAndGetOutput(repoPath, "git", "log", "--reverse", "--format=%as", "--", taskId);
        return dateStr.isEmpty() ? null : LocalDate.parse(dateStr.split("\n")[0]);
    }

    public LocalDate getHardDeadlineDate(Path repoPath) {
        String dateStr = executeAndGetOutput(repoPath, "git", "log", "-1", "--format=%as");
        return dateStr.isEmpty() ? null : LocalDate.parse(dateStr.trim());
    }

    private String executeAndGetOutput(Path workingDir, String... command) {
        try {
            Process process = new ProcessBuilder(command)
                    .directory(workingDir.toFile())
                    .start();
            return new String(process.getInputStream().readAllBytes()).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public LocalDate getFirstCommitDate(Path repoPath, String branchName) {
        try {
            Process process = new ProcessBuilder("git", "log", branchName, "--reverse", "--format=%as")
                    .directory(repoPath.toFile())
                    .start();

            String output = new String(process.getInputStream().readAllBytes()).trim();
            if (output.isEmpty()) return LocalDate.now();

            return LocalDate.parse(output.split("\n")[0]);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}