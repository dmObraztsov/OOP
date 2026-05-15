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

        Logger.info("Начало массовой проверки групп: " + config.getGroups().size());

        return config.getGroups().stream()
                .flatMap(group -> {
                    Path groupDir = workspace.resolve(group.name());

                    return group.students().parallelStream().map(student -> {
                        String sid = student.githubId();
                        Logger.info("[" + sid + "] Начинаю обработку студента...");

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

                            Logger.debug("[" + sid + "] Активность рассчитана: S1=" + act1 + ", S2=" + act2);

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

                            Logger.info("[" + sid + "] Проверка завершена. Баллы: S1=" + t1 + ", S2=" + t2);

                            return new StudentResult(
                                    student, p1Res, p2Res, t1, t2,
                                    strategy1.mapTotalToGrade(t1, act1, true),
                                    strategy2.mapTotalToGrade(t2, act2, true),
                                    act1, act2
                            );
                        } catch (Exception e) {
                            Logger.error("[" + sid + "] Критическая ошибка при обработке: " + e.getMessage());
                            return null;
                        }
                    });
                })
                .filter(java.util.Objects::nonNull)
                .toList();
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