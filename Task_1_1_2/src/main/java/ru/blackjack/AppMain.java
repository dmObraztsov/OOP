package ru.blackjack;


import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import ru.blackjack.ui.ConsoleUserIo;


/**
 *
 */
public class AppMain {

    public static void main(String[] args) {
        // Гарантируем UTF-8 для вывода:
        try {
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // если не получилось — продолжим со стандартным потоком
        }

        // Гарантируем UTF-8 для ввода:
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            var userIo = new ConsoleUserIo(scanner);
            var game = new ru.blackjack.game.BlackJackGame(userIo);
            game.startGameLoopUntilUserStops();
        }
    }
}
