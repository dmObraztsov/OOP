package service;

import core.logic.Semester1Strategy;
import core.model.*;
import infrastructure.git.GitClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FullFlowIntegrationTest {

    @TempDir
    Path sharedWorkspace;

    private GradingManager gradingManager;
    private CourseConfiguration config;

    @BeforeEach
    void setUp() {
        GitClient fakeGitClient = new GitClient() {
            @Override public void cloneRepository(String url, Path dest) {}
            @Override public void checkoutBranch(Path repo, String branch) {}
            @Override public boolean testConnection(String url) { return true; }
            @Override public void fetchAll(Path repo) {}

            @Override
            public java.util.List<String> getCommitDates(Path repoPath) {
                return java.util.List.of();
            }

            @Override
            public java.time.LocalDate getFirstCommitDate(Path repoPath, String branchName) {
                return java.time.LocalDate.now();
            }

            @Override
            public java.time.LocalDate getLastCommitDate(Path repoPath, String branchName) {
                return java.time.LocalDate.now();
            }
        };

        infrastructure.build.BuildRunner fakeBuildRunner = new infrastructure.build.GradleBuildRunner() {
            @Override public infrastructure.build.BuildResult runCompile(Path p) {
                return new infrastructure.build.BuildResult(true, true, 0, 0, 0);
            }
            @Override public boolean runCheckstyle(Path p) { return true; }
            @Override public boolean runTests(Path p) { return true; }
        };

        gradingManager = new GradingManager(
                fakeGitClient,
                fakeBuildRunner,
                new core.logic.Semester1Strategy(),
                new core.logic.Semester1Strategy(),
                new service.ValidationService(),
                new infrastructure.build.TestResultParser(),
                sharedWorkspace
        );

        config = new core.model.CourseConfiguration();
    }

    @Test
    void testEndToEndGrading() throws IOException {

        String studentId = "test_student";
        Path groupDir = sharedWorkspace.resolve("M3101");
        Path studentRepo = groupDir.resolve(studentId);
        Files.createDirectories(studentRepo);

        Path gradlew = studentRepo.resolve(System.getProperty("os.name").toLowerCase().contains("win")
                ? "gradlew.bat" : "gradlew");
        Files.createFile(gradlew);

        String taskId = "Task_1_1";
        Path taskPath = studentRepo.resolve(taskId);
        Files.createDirectories(taskPath);

        Files.createDirectories(studentRepo.resolve(".git"));

        Path testResults = taskPath.resolve("build/test-results/test");
        Files.createDirectories(testResults);
        Files.writeString(testResults.resolve("TEST-Result.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="5" failures="0" errors="0" skipped="0">
                </testsuite>
                """);

        Path jacocoDir = taskPath.resolve("build/reports/jacoco/test");
        Files.createDirectories(jacocoDir);
        Files.writeString(jacocoDir.resolve("jacocoTestReport.xml"), """
                <?xml version="1.0" encoding="UTF-8"?>
                <report name="Task_1_1">
                    <counter type="INSTRUCTION" missed="0" covered="100"/>
                </report>
                """);

        Task task = new Task(taskId, "Test Task", 10.0, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        config.addTask(task);

        Student student = new Student(studentId, "Имя Тестович", "https://github.com/test/repo", "M3101");
        Group group = new Group("M3101", List.of(student));
        config.addGroup(group);

        List<StudentResult> results = gradingManager.processAll(config, new Semester1Strategy(), new Semester1Strategy());

        assertFalse(results.isEmpty(), "Список результатов не должен быть пустым");

        StudentResult res = results.get(0);
        assertEquals(studentId, res.student().githubId());

        TaskResult taskRes = res.part1Results().get(taskId);
        assertNotNull(taskRes, "Результат по задаче должен существовать");

        assertEquals(5, taskRes.tests().total(), "Должно быть 5 тестов");
        assertEquals(100.0, taskRes.coverage(), "Покрытие должно быть 100%");

        assertEquals(10.0, res.totalPart1(), "Итоговый балл должен быть 10.0");
    }
}