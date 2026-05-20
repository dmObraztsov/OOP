package service;

import core.logic.GradingStrategy;
import core.model.*;
import core.util.Logger;
import infrastructure.build.*;
import infrastructure.git.ConsoleGitClient;
import infrastructure.git.GitClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GradingManager {
    private final GitClient gitClient;
    private final BuildRunner buildRunner;
    private final GradingStrategy strategy1;
    private final GradingStrategy strategy2;
    private final ValidationService validationService;
    private final TestResultParser testParser;
    private final Path workspace;

    public GradingManager(GitClient gitClient, BuildRunner buildRunner,
                          GradingStrategy s1, GradingStrategy s2,
                          ValidationService validationService, TestResultParser testParser,
                          Path workspace) {
        this.gitClient = gitClient;
        this.buildRunner = buildRunner;
        this.strategy1 = s1;
        this.strategy2 = s2;
        this.validationService = validationService;
        this.testParser = testParser;
        this.workspace = workspace;
    }

    public List<StudentResult> processAll(CourseConfiguration config,
                                          GradingStrategy strategy1,
                                          GradingStrategy strategy2) {

        ZonedDateTime s1Start = ZonedDateTime.parse("2025-09-01T00:00:00+03:00[Europe/Moscow]");
        ZonedDateTime s1End = ZonedDateTime.parse("2025-12-31T23:59:59+03:00[Europe/Moscow]");
        ZonedDateTime s2Start = ZonedDateTime.parse("2026-02-01T00:00:00+03:00[Europe/Moscow]");
        ZonedDateTime s2End = ZonedDateTime.parse("2026-05-31T23:59:59+03:00[Europe/Moscow]");

        Logger.info("Подготовка к параллельной обработке...");

        var allTasks = config.getGroups().stream()
                .flatMap(group -> group.students().stream()
                        .map(student -> new AbstractMap.SimpleEntry<>(group, student)))
                .toList();

        Logger.info("Запуск массовой проверки. Студентов к обработке: " + allTasks.size());

        java.util.concurrent.ForkJoinPool customPool = new java.util.concurrent.ForkJoinPool(8);

        try {
            return customPool.submit(() ->
                    allTasks.parallelStream().map(entry -> {
                                Group group = entry.getKey();
                                Student student = entry.getValue();
                                String sid = student.githubId();

                                Logger.info("[" + sid + "] Начинаю обработку в потоке: " + Thread.currentThread().getName());

                                Path groupDir = workspace.resolve(group.name());
                                Path studentDir = groupDir.resolve(sid);

                                try {
                                    if (!Files.exists(studentDir.resolve(".git"))) {
                                        Logger.info("[" + sid + "] Репозиторий отсутствует. Клонирую...");
                                        gitClient.cloneRepository(student.repoUrl(), studentDir);
                                    } else {
                                        Logger.debug("[" + sid + "] Репозиторий найден. Обновляю (fetch)...");
                                        gitClient.fetchAll(studentDir);
                                    }

                                    List<String> allDates = gitClient.getCommitDates(studentDir);
                                    double act1 = calculateActivity(allDates, s1Start, s1End);
                                    double act2 = calculateActivity(allDates, s2Start, s2End);

                                    Map<String, TaskResult> p1Res = new LinkedHashMap<>();
                                    Map<String, TaskResult> p2Res = new LinkedHashMap<>();
                                    double t1 = 0, t2 = 0;

                                    for (Task task : config.getTasks().values()) {
                                        boolean isPart1 = task.id().startsWith("Task_1");
                                        GradingStrategy currentStrat = isPart1 ? strategy1 : strategy2;

                                        TaskResult res = evaluateTask(student, task, groupDir, currentStrat);



                                        if (isPart1) {
                                            p1Res.put(task.id(), res);
                                            t1 += res.score();
                                        } else {
                                            p2Res.put(task.id(), res);
                                            t2 += res.score();
                                        }
                                    }
                                    Logger.info("[" + sid + "] Проверка завершена. S1=" + t1 + ", S2=" + t2);

                                    return new StudentResult(
                                            student, p1Res, p2Res, t1, t2,
                                            strategy1.mapTotalToGrade(t1, act1, true),
                                            strategy2.mapTotalToGrade(t2, act2, true),
                                            act1, act2
                                    );
                                } catch (Exception e) {
                                    Logger.error("[" + sid + "] Критическая ошибка: " + e.getMessage());
                                    return null;
                                }
                            })
                            .filter(java.util.Objects::nonNull)
                            .toList()
            ).get();

        } catch (Exception e) {
            Logger.error("Ошибка при выполнении параллельного пула: " + e.getMessage());
            return List.of();
        } finally {
            customPool.shutdown();
        }
    }

    private TaskResult evaluateTask(Student student, Task task, Path groupDir, GradingStrategy currentStrategy) {
        String sid = student.githubId();
        Path studentDir = groupDir.resolve(sid);
        GradleBuildRunner gradleRunner = (GradleBuildRunner) buildRunner;

        Logger.debug("[" + sid + ":" + task.id() + "] Оценка задачи...");

        try {
            gitClient.checkoutBranch(studentDir, task.id());

            Path taskPath = studentDir.resolve(task.id());
            if (!Files.exists(taskPath)) {
                Logger.error("[" + sid + ":" + task.id() + "] Папка задачи не найдена в репозитории.");
                return TaskResult.failed();
            }

            BuildResult compileResult = gradleRunner.runCompile(taskPath);
            if (!compileResult.compileSuccess()) {
                Logger.error("[" + sid + ":" + task.id() + "] Ошибка компиляции.");
                return TaskResult.failed();
            }

            boolean styleOk = gradleRunner.runCheckstyle(taskPath);

            gradleRunner.runTests(taskPath);
            Path xmlPath = taskPath.resolve("build/test-results/test");
            TestSummary tests = testParser.parseDirectory(xmlPath);

            Path jacocoXml = taskPath.resolve("build/reports/jacoco/test/jacocoTestReport.xml");
            double coverage = 0.0;

            if (Files.exists(jacocoXml)) {
                coverage = new CoverageParser().parseCoverage(jacocoXml);
            } else {
                Logger.debug("[" + sid + ":" + task.id() + "] JaCoCo XML не найден.");
            }

            LocalDate firstCommit = gitClient.getFirstCommitDate(studentDir, task.id());
            LocalDate lastCommit = gitClient.getLastCommitDate(studentDir, task.id());


            Logger.debug("[" + sid + ":" + task.id() + "] --- ПРОВЕРКА ДАТ ДЛЯ СТРАТЕГИИ ---");
            Logger.debug("[" + sid + ":" + task.id() + "] Soft Deadline в конфиге: " + task.softDeadline());
            Logger.debug("[" + sid + ":" + task.id() + "] Hard Deadline в конфиге: " + task.hardDeadline());
            Logger.debug("[" + sid + ":" + task.id() + "] Дата первого коммита (softSubmit): " + firstCommit);
            Logger.debug("[" + sid + ":" + task.id() + "] Дата последнего коммита (hardApprove): " + lastCommit);

            if (firstCommit != null && lastCommit != null) {
                boolean metSoft = !firstCommit.isAfter(task.softDeadline());
                boolean metHard = !lastCommit.isAfter(task.hardDeadline());
                Logger.debug("[" + sid + ":" + task.id() + "] Результат: metSoft=" + metSoft + ", metHard=" + metHard);
            } else {
                Logger.error("[" + sid + ":" + task.id() + "] ВНИМАНИЕ: Одна из дат NULL!");
            }

            double score = currentStrategy.calculateTaskScore(task, firstCommit, lastCommit, false);

            Logger.debug("[" + sid + ":" + task.id() + "] Score=" + score + " | Style=" + styleOk + " | Coverage=" + String.format("%.1f%%", coverage));

            return new TaskResult(true, styleOk, tests, coverage, score);

        } catch (Exception e) {
            Logger.error("[" + sid + ":" + task.id() + "] Исключение в evaluateTask: " + e.getMessage());
            return TaskResult.failed();
        }
    }

    private double calculateActivity(List<String> isoDates, ZonedDateTime start, ZonedDateTime end) {
        if (isoDates.isEmpty()) return 0.0;

        long totalWeeks = java.time.temporal.ChronoUnit.WEEKS.between(start, end) + 1;

        var activeWeeks = isoDates.stream()
                .map(ZonedDateTime::parse)
                .filter(d -> !d.isBefore(start) && !d.isAfter(end))
                .map(d -> d.getYear() + "-" + d.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR))
                .distinct()
                .count();

        return Math.min(1.0, (double) activeWeeks / totalWeeks);
    }
}