import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubstringSearch {

    public static List<Integer> findOccurrences(String fileName, String pattern) throws IOException {
        List<Integer> result = new ArrayList<>();

        try (Reader reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8)) {
            int ch;
            int j = 0; // индекс в паттерне
            int pos = 0; // индекс по символам
            int[] lps = computeLPS(pattern);

            while ((ch = reader.read()) != -1) {
                char current = (char) ch;

                while (j > 0 && current != pattern.charAt(j)) {
                    j = lps[j - 1];
                }

                if (current == pattern.charAt(j)) {
                    j++;
                    if (j == pattern.length()) {
                        result.add(pos - pattern.length() + 1);
                        j = lps[j - 1];
                    }
                }
                pos++;
            }
        }
        return result;
    }

    // Построение массива lps (longest proper prefix)
    private static int[] computeLPS(String pat) {
        int[] lps = new int[pat.length()];
        int len = 0;
        for (int i = 1; i < pat.length(); ) {
            if (pat.charAt(i) == pat.charAt(len)) {
                lps[i++] = ++len;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i++] = 0;
                }
            }
        }
        return lps;
    }

    public static void main(String[] args) throws IOException {
        String fileName = "bigdata.txt";
        String pattern = "бра";

        List<Integer> occurrences = findOccurrences(fileName, pattern);
        System.out.println("Count: " + occurrences.toArray().length);
        System.out.println("Indexes: " + occurrences);
    }
}
