package service;

import core.logic.Semester1Strategy;
import core.model.*;
import infrastructure.build.*;
import infrastructure.git.GitClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FullFlowIntegrationTest {

    @TempDir
    Path sharedWorkspace;

    @Mock
    private GitClient gitClient;

    @Mock
    private GradleBuildRunner buildRunner;

    private GradingManager gradingManager;
    private CourseConfiguration config;

    @BeforeEach
    void setUp() {
        gradingManager = new GradingManager(
                gitClient,
                buildRunner,
                new Semester1Strategy(),
                new Semester1Strategy(),
                new ValidationService(),
                new TestResultParser(),
                sharedWorkspace
        );
        config = new CourseConfiguration();
    }

    @Test
    void testEndToEndGrading() throws IOException {
        // given
        String studentId = "test_student";
        String groupId = "M3101";
        String taskId = "Task_1_1";

        Path groupDir = sharedWorkspace.resolve(groupId);
        Path studentRepo = groupDir.resolve(studentId);
        Files.createDirectories(studentRepo);

        Files.createDirectories(studentRepo.resolve(".git"));

        Path taskPath = studentRepo.resolve(taskId);
        Files.createDirectories(taskPath);

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

        List<String> mockDates = List.of("2026-05-20T10:00:00+03:00[Europe/Moscow]");
        when(gitClient.getCommitDates(studentRepo)).thenReturn(mockDates);

        when(gitClient.getFirstCommitDate(studentRepo, taskId)).thenReturn(LocalDate.now());
        when(gitClient.getLastCommitDate(studentRepo, taskId)).thenReturn(LocalDate.now());

        BuildResult successBuild = new BuildResult(true, true, 0, 0, 0);
        when(buildRunner.runCompile(taskPath)).thenReturn(successBuild);
        when(buildRunner.runCheckstyle(taskPath)).thenReturn(true);

        Task task = new Task(taskId, "Test Task", 10.0, LocalDate.now().plusDays(5), LocalDate.now().plusDays(10));
        config.addTask(task);

        Student student = new Student(studentId, "Имя Тестович", "https://github.com/test/repo", groupId);
        Group group = new Group(groupId, List.of(student));
        config.addGroup(group);

        // when
        List<StudentResult> results = gradingManager.processAll(config, new Semester1Strategy(), new Semester1Strategy());

        // then
        assertFalse(results.isEmpty(), "Список результатов не должен быть пустым");

        StudentResult res = results.get(0);
        assertEquals(studentId, res.student().githubId());

        TaskResult taskRes = res.part1Results().get(taskId);
        assertNotNull(taskRes, "Результат по задаче должен существовать");

        assertEquals(5, taskRes.tests().total(), "Должно быть успешно распарсено 5 тестов");
        assertEquals(100.0, taskRes.coverage(), "Покрытие должно быть определено как 100%");

        assertEquals(10.0, res.totalPart1(), "Итоговый балл должен быть 10.0 (без штрафов)");
    }
}