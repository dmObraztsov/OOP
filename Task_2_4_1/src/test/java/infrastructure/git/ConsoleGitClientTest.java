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
        ConsoleGitClient client = new ConsoleGitClient();
        boolean result = client.testConnection("not_a_protocol://invalid-url");
        assertFalse(result, "Соединение должно отсутствовать для некорректного протокола/URL");
    }

    @Test
    void testGetDatesOnEmptyDirectory() {
        ConsoleGitClient client = new ConsoleGitClient();
        LocalDate date = client.getFirstCommitDate(tempDir, "main");
        assertEquals(LocalDate.now(), date);
    }
}