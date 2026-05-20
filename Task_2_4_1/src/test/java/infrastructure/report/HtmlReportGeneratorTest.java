package infrastructure.report;

import core.model.Student;
import core.model.StudentResult;
import core.model.TaskResult;
import infrastructure.build.TestSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlReportGeneratorTest {

    @TempDir
    Path tempDir;

    @Test
    void testGenerateWithEmptyResults() throws IOException {
        // given
        HtmlReportGenerator generator = new HtmlReportGenerator();
        Path reportPath = tempDir.resolve("report.html");

        // when
        generator.generate(Collections.emptyList(), reportPath);
        String content = Files.readString(reportPath);

        // then
        assertTrue(Files.exists(reportPath));
        assertTrue(content.contains("Нет данных для отчета"));
    }

    @Test
    void testGenerateWithData() throws IOException {
        // given
        HtmlReportGenerator generator = new HtmlReportGenerator();
        Path reportPath = tempDir.resolve("report_full.html");

        Student student = new Student("id1", "Test Student", "repo", "Group1");

        Map<String, TaskResult> p1 = new HashMap<>();
        p1.put("Task_1_1", new TaskResult(true, true, new TestSummary(5, 5, 0), 100.0, 10.0));

        StudentResult result = new StudentResult(
                student, p1, new HashMap<>(),
                10.0, 0.0, "отлично", "н/я", 1.0, 0.0
        );

        // when
        generator.generate(List.of(result), reportPath);
        String content = Files.readString(reportPath);

        // then
        assertTrue(Files.exists(reportPath));
        assertTrue(content.contains("Test Student"), "Имя студента должно быть в отчете");
        assertTrue(content.contains("Task_1_1"), "ID задачи должен быть в отчете");
    }
}