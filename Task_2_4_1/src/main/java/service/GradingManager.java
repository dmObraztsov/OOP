package service;

import core.logic.GradingStrategy;
import core.model.*;
import infrastructure.build.BuildRunner;
import infrastructure.build.TestResultParser;
import infrastructure.build.TestSummary;
import infrastructure.git.ConsoleGitClient;
import infrastructure.git.GitClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GradingManager {
    private final GitClient gitClient;
    private final BuildRunner buildRunner;
    private final GradingStrategy strategy;
    private final ValidationService validationService;
    private final TestResultParser testParser;
    private final Path workspace;



    public GradingManager(GitClient gitClient, BuildRunner buildRunner,
                          GradingStrategy strategy, ValidationService validationService, TestResultParser testParser, Path workspace) {
        this.gitClient = gitClient;
        this.buildRunner = buildRunner;
        this.strategy = strategy;
        this.validationService = validationService;
        this.testParser = testParser;
        this.workspace = workspace;
    }

    public List<StudentResult> processAll(CourseConfiguration config) {
        List<StudentResult> allResults = new ArrayList<>();

        System.out.println("[DEBUG] processAll started with " + config.getGroups().size() + " groups");
        System.out.println("[DEBUG] Using workspace: " + workspace.toAbsolutePath());

        for (Group group : config.getGroups()) {
            Path groupDir = workspace.resolve(group.name());

            for (Student student : group.students()) {
                Map<String, Double> taskScores = new LinkedHashMap<>();
                double total = 0;

                Path studentDir = groupDir.resolve(student.githubId());
                if (!Files.exists(studentDir)) {
                    System.out.println("[DEBUG] Cloning repo for student: " + student.githubId());
                    gitClient.cloneRepository(student.repoUrl(), studentDir);
                }

                for (Task task : config.getTasks().values()) {
                    double score = evaluateTask(student, task, groupDir);
                    taskScores.put(task.id(), score);
                    total += score;
                }

                String grade = strategy.mapTotalToGrade(total, true);
                allResults.add(new StudentResult(student, taskScores, total, grade));
            }
        }
        return allResults;
    }

    private double evaluateTask(Student student, Task task, Path groupDir) {
        Path studentDir = groupDir.resolve(student.githubId());
        String branchName = task.id();

        System.out.println("\n[DEBUG] --- Evaluating Task: " + task.id() + " ---");
        System.out.println("[DEBUG] Student: " + student.fullName() + " (ID: " + student.githubId() + ")");

        try {
            gitClient.checkoutBranch(studentDir, branchName);

            Path taskPath = studentDir.resolve(task.id());
            if (!Files.exists(taskPath)) {
                System.out.println("[DEBUG] [!] ERROR: Folder '" + task.id() + "' NOT FOUND!");
                return 0.0;
            }

            System.out.println("[DEBUG] SUCCESS: Folder found. Starting build process...");
            var buildResult = buildRunner.run(taskPath);

            if (buildResult.compileSuccess()) {
                Path xmlResults = taskPath.resolve("build").resolve("test-results").resolve("test");
                TestSummary summary = testParser.parseDirectory(xmlResults);

                System.out.println("[DEBUG] " + summary); // Tests: X/Y passed...

                LocalDate submitDate = ((ConsoleGitClient) gitClient).getFirstCommitDate(studentDir, branchName);
                System.out.println("[DEBUG] First commit detected on: " + submitDate);

                double score = strategy.calculateTaskScore(task, submitDate, LocalDate.now(), summary.allPassed());

                System.out.println("[DEBUG] Final calculated score: " + score);
                return score;
            } else {
                System.out.println("[DEBUG] [!] Build failed. Score: 0.0");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] [!] EXCEPTION: " + e.getMessage());
        }
        return 0.0;
    }

    private void processStudent(Student student, CourseConfiguration config, Path groupDir) throws Exception {
        Path studentDir = groupDir.resolve(student.githubId());

        if (!Files.exists(groupDir)) {
            Files.createDirectories(groupDir);
        }

        if (!Files.exists(studentDir)) {
            gitClient.cloneRepository(student.repoUrl(), studentDir);
        }

        for (Task task : config.getTasks().values()) {
            String branchName = task.id();

            System.out.println("Проверка " + student.githubId() + " [" + branchName + "]");

            gitClient.checkoutBranch(studentDir, branchName);

            Path taskProjectPath = studentDir.resolve(task.id());

            if (Files.exists(taskProjectPath)) {
                var buildResult = buildRunner.run(taskProjectPath);
                Path xmlResults = taskProjectPath.resolve("build/test-results/test");
                TestSummary summary = testParser.parseDirectory(xmlResults);

                System.out.println("  Тестов пройдено: " + summary.passed() + " из " + summary.total());

                if (buildResult.compileSuccess() && validationService.checkGoogleJavaStyle(taskProjectPath)) {
                    double coverage = validationService.getTestCoverage(taskProjectPath);

                    double score = strategy.calculateTaskScore(task, LocalDate.now(), LocalDate.now(), false);

                    System.out.println("  Результат: " + score + " баллов. Тесты: " + buildResult.testsPassed());
                }
            }
        }
    }
}