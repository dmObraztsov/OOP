package infrastructure.build;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TestResultParser {

    public TestSummary parseDirectory(Path testResultsPath) {
        if (!Files.exists(testResultsPath)) {
            return new TestSummary(0, 0, 0);
        }

        int total = 0, failed = 0, skipped = 0;

        try (Stream<Path> files = Files.list(testResultsPath)) {
            var xmlFiles = files.filter(p -> p.toString().endsWith(".xml")).toList();

            for (Path file : xmlFiles) {
                TestSummary summary = parseFile(file.toFile());
                total += summary.total();
                failed += summary.failed();
                skipped += summary.skipped();
            }
        } catch (Exception e) {
            System.err.println("Ошибка при парсинге XML тестов: " + e.getMessage());
        }

        return new TestSummary(total, failed, skipped);
    }

    private TestSummary parseFile(File file) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        Element root = doc.getDocumentElement(); // <testsuite>

        int tests = Integer.parseInt(root.getAttribute("tests"));
        int failures = Integer.parseInt(root.getAttribute("failures"));
        int errors = Integer.parseInt(root.getAttribute("errors"));
        int skipped = Integer.parseInt(root.getAttribute("skipped").isEmpty() ? "0" : root.getAttribute("skipped"));

        return new TestSummary(tests, failures + errors, skipped);
    }
}

