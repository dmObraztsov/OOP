package infrastructure.build;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ParserIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testFullCoverageParsing() throws IOException {
        // given
        Path jacocoFile = tempDir.resolve("jacoco.xml");
        String content = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <report name="TestReport">
                    <counter type="INSTRUCTION" missed="20" covered="80"/>
                    <counter type="LINE" missed="5" covered="15"/>
                </report>
                """;
        Files.writeString(jacocoFile, content);
        CoverageParser parser = new CoverageParser();


        // when
        double coverage = parser.parseCoverage(jacocoFile);

        // then
        assertEquals(80.0, coverage, 0.01, "Покрытие должно быть 80%");
    }

    @Test
    void testTestResultParsing() throws IOException {
        // given
        Path testDir = tempDir.resolve("test-results");
        Files.createDirectories(testDir);
        String testXml1 = """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="10" failures="2" errors="0" skipped="1">
                </testsuite>
                """;
        String testXml2 = """
                <?xml version="1.0" encoding="UTF-8"?>
                <testsuite tests="5" failures="0" errors="1" skipped="0">
                </testsuite>
                """;

        Files.writeString(testDir.resolve("TEST-1.xml"), testXml1);
        Files.writeString(testDir.resolve("TEST-2.xml"), testXml2);
        TestResultParser parser = new TestResultParser();

        // when
        TestSummary summary = parser.parseDirectory(testDir);

        // then
        assertEquals(15, summary.total());
        assertEquals(3, summary.failed());
        assertEquals(1, summary.skipped());
        assertEquals(11, summary.passed());
    }

    @Test
    void testParserHandlesMissingFiles() {
        // given
        CoverageParser coverageParser = new CoverageParser();
        TestResultParser testParser = new TestResultParser();

        // then
        assertDoesNotThrow(() -> {
            // when
            double res = coverageParser.parseCoverage(Path.of("invalid_path_123.xml"));
            assertEquals(0.0, res);

            TestSummary summary = testParser.parseDirectory(Path.of("invalid_dir_123"));
            assertEquals(0, summary.total());
        });
    }
}