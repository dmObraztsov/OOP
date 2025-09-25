package ru.blackjack;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import ru.blackjack.ui.ConsoleUserIo;
import ru.blackjack.ui.RussianPhrases;

public class AppMain {


    public static void main(String[] args) {
        try {
            System.setOut(createUtf8PrintStream(FileDescriptor.out));
            System.setErr(createUtf8PrintStream(FileDescriptor.err));
        } catch (Exception ignored) {
            // если не получилось — продолжим со стандартным потоком
        }

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            var userIo = new ConsoleUserIo(scanner, new RussianPhrases());
            var game = new ru.blackjack.game.BlackJackGame(userIo);
            game.startGameLoopUntilUserStops();
        }
    }

    private static PrintStream createUtf8PrintStream(FileDescriptor fd) throws Exception {
        return new PrintStream(new FileOutputStream(fd), true, StandardCharsets.UTF_8);
    }
}
