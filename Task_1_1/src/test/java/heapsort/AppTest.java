package heapsort;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppTest {

    private String runMainAndCaptureOutput(String... args) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos));
            App.main(args);
        } finally {
            System.out.flush();
            System.setOut(originalOut);
        }
        return baos.toString().trim();
    }

    @Test
    void printsSortedDemoArrayWhenNoArgs() {
        String out = runMainAndCaptureOutput(); // без аргументов берётся демо-массив
        assertEquals("[-4, 0, 1, 2, 3, 5, 5, 9]", out);
    }

    @Test
    void sortsNumbersFromArgs() {
        String out = runMainAndCaptureOutput("7", "3", "9", "-1", "0", "7");
        assertEquals("[-1, 0, 3, 7, 7, 9]", out);
    }

    @Test
    void throwsOnNonIntegerArg() {
        assertThrows(NumberFormatException.class, () -> App.main(new String[]{"42", "oops"}));
    }
}
