import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SubstringSearchTest {

    private Path tempFile;

    @AfterEach
    void cleanup() throws IOException {
        if (tempFile != null) {
            Files.deleteIfExists(tempFile);
        }
    }

    private Path createTempFileWithContent(String content) throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        return tempFile;
    }

    @Test
    void testSimpleAscii() throws IOException {
        Path file = createTempFileWithContent("ababa");
        List<Integer> result = SubstringSearch.findOccurrences(file.toString(), "aba");

        assertEquals(List.of(0, 2), result);
    }

    @Test
    void testUtf8Russian() throws IOException {
        Path file = createTempFileWithContent("привет брабра мир");
        List<Integer> result = SubstringSearch.findOccurrences(file.toString(), "бра");

        assertEquals(List.of(7, 10), result);
    }

    @Test
    void testNoOccurrences() throws IOException {
        Path file = createTempFileWithContent("abcdefg");
        List<Integer> result = SubstringSearch.findOccurrences(file.toString(), "zzz");

        assertTrue(result.isEmpty());
    }

    @Test
    void testOccurrenceAtEnd() throws IOException {
        Path file = createTempFileWithContent("12345бра");
        List<Integer> result = SubstringSearch.findOccurrences(file.toString(), "бра");

        assertEquals(List.of(5), result);
    }

    @Test
    void testOverlappingMatches() throws IOException {
        Path file = createTempFileWithContent("aaaaa");
        List<Integer> result = SubstringSearch.findOccurrences(file.toString(), "aaa");

        // позиции: 0, 1, 2
        assertEquals(List.of(0, 1, 2), result);
    }

    @Test
    void testLargeGeneratedFile() throws IOException {
        tempFile = Files.createTempFile("big", ".txt");

        String block = "абвгдеж12345бра";
        int repeats = 200_000;

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            for (int i = 0; i < repeats; i++) {
                writer.write(block);
            }
        }

        int blockLen = block.length();
        int patternPos = blockLen - 3;
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < repeats; i++) {
            expected.add(i * blockLen + patternPos);
        }

        List<Integer> result = SubstringSearch.findOccurrences(tempFile.toString(), "бра");

        assertEquals(expected, result);
    }

    @Test
    void testHugeStreamedFile() throws IOException {
        tempFile = Files.createTempFile("huge", ".txt");

        String filler = "ABCDEФЫВА";
        String target = "бра";

        int chunks = 500_000;
        int hitEvery = 10000;

        long expectedIndex = 0;
        List<Integer> expected = new ArrayList<>();

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            for (int i = 0; i < chunks; i++) {

                writer.write(filler);
                expectedIndex += filler.length();

                if (i % hitEvery == 0) {
                    writer.write(target);
                    expected.add((int) expectedIndex);
                    expectedIndex += target.length();
                }
            }
        }

        List<Integer> result = SubstringSearch.findOccurrences(tempFile.toString(), target);

        assertEquals(expected, result);
    }
}
