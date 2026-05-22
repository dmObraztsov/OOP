package dsl;

import core.model.CourseConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void testLoadConfigurationFromDsl() throws IOException {
        // given
        Path dslFile = tempDir.resolve("course.groovy");
        String dslContent = """
            tasks {
                task id: "T1", name: "Task 1", max: 10.0, soft: "2026-01-01", hard: "2026-01-10"
            }
            groups {
                group("M3101") {
                    student id: "s1", name: "Ivanov", repo: "http://github.com/repo"
                }
            }
            """;
        Files.writeString(dslFile, dslContent);

        // when
        ConfigurationLoader loader = new ConfigurationLoader();
        CourseConfiguration config = loader.load(dslFile.toFile());

        // then
        assertEquals(1, config.getTasks().size());
        assertEquals(1, config.getGroups().size());
        assertTrue(config.getTasks().containsKey("T1"));
        assertEquals("Ivanov", config.getGroups().get(0).students().get(0).fullName());
    }
}