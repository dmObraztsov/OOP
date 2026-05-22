package infrastructure.git;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleGitClientTest {

    @TempDir
    Path tempDir;

    @Test
    void testTestConnectionFailure() {
        // given
        ConsoleGitClient client = new ConsoleGitClient();
        String url = "not_a_protocol://invalid-url";

        // when
        boolean result = client.testConnection(url);

        // then
        assertFalse(result, "Соединение должно отсутствовать для некорректного протокола/URL");
    }

    @Test
    void testGetDatesOnEmptyDirectory() {
        // given
        ConsoleGitClient client = new ConsoleGitClient();

        // when
        LocalDate date = client.getFirstCommitDate(tempDir, "main");

        // then
        assertEquals(LocalDate.now(), date);
    }
}