package infrastructure.build;

import core.util.Logger;
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
            Logger.debug("Директория с результатами тестов не найдена: " + testResultsPath);
            return new TestSummary(0, 0, 0);
        }

        int total = 0, failed = 0, skipped = 0;

        try (Stream<Path> files = Files.list(testResultsPath)) {
            var xmlFiles = files.filter(p -> p.toString().endsWith(".xml")).toList();

            for (Path file : xmlFiles) {
                try {
                    TestSummary summary = parseFile(file.toFile());
                    total += summary.total();
                    failed += summary.failed();
                    skipped += summary.skipped();
                } catch (Exception e) {
                    Logger.error("Ошибка парсинга файла теста " + file.getFileName() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Logger.error("Ошибка при чтении директории тестов: " + e.getMessage());
        }

        return new TestSummary(total, failed, skipped);
    }

    private TestSummary parseFile(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        Document doc = factory.newDocumentBuilder().parse(file);
        Element root = doc.getDocumentElement();

        int tests = getIntAttribute(root, "tests");
        int failures = getIntAttribute(root, "failures");
        int errors = getIntAttribute(root, "errors");
        int skipped = getIntAttribute(root, "skipped");

        return new TestSummary(tests, failures + errors, skipped);
    }

    private int getIntAttribute(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}