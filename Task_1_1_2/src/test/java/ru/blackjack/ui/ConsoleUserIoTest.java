package ru.blackjack.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;
import ru.blackjack.cards.Suit;
import ru.blackjack.game.Dealer;
import ru.blackjack.game.Participant;


/**
 * Тесты консольного интерфейса {@link ConsoleUserIo}.
 * Проверяются приветствие, чтение решений и форматирование рук.
 */
class ConsoleUserIoTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    /**
     * Перенаправляет {@code System.out} в буфер перед каждым тестом.
     *
     * @throws Exception при ошибке перенастройки потоков
     */
    @BeforeEach
    void setUp() throws Exception {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
    }

    /**
     * Возвращает {@code System.out} на место после каждого теста.
     */
    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Проверяет, что выводится приветствие.
     */
    @Test
    void printsWelcomeAndUsingDeck() {
        Scanner sc = new Scanner(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8);
        ConsoleUserIo io = new ConsoleUserIo(sc);

        io.printToUserConsoleWelcomeMessage();

        String text = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Добро пожаловать в Блэкджек!"));
    }

    /**
     * Проверяет чтение решения «взять/стоп» из сканера.
     */
    @Test
    void askHitOrStandReadsFromScanner() {
        byte[] input = "1\n".getBytes(StandardCharsets.UTF_8);
        Scanner sc = new Scanner(new ByteArrayInputStream(input), StandardCharsets.UTF_8);
        ConsoleUserIo io = new ConsoleUserIo(sc);

        int decision = io.askUserHitOrStand();
        assertEquals(1, decision);
    }

    /**
     * Проверяет форматирование рук: после раздачи (скрытая карта дилера)
     * и после раскрытия карты с выводом сумм.
     */
    @Test
    void handsFormattingWithHiddenAndSums() {
        Scanner sc = new Scanner(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8);
        ConsoleUserIo io = new ConsoleUserIo(sc);

        Participant player = new Participant("Игрок");
        Dealer dealer = new Dealer("Дилер");

        player.receiveCardFromShoe(new Card(Suit.SPADES, Rank.QUEEN));
        player.receiveCardFromShoe(new Card(Suit.HEARTS, Rank.THREE));

        dealer.receiveOpenCardFromShoe(new Card(Suit.CLUBS, Rank.ACE));
        dealer.receiveHoleCardFromShoe(new Card(Suit.DIAMONDS, Rank.THREE));

        io.printToUserConsoleHandsAfterDeal(player, dealer);
        String text1 = outContent.toString(StandardCharsets.UTF_8);
        assertTrue(text1.contains("Ваши карты: [Дама Пики (10), Тройка Червы (3)] > 13"));
        assertTrue(text1.contains("Карты дилера: [Туз Трефы (11), <закрытая карта>]"));

        outContent.reset();
        dealer.revealHoleCard();
        io.printToUserConsoleHandsWithSums(player, dealer);
        String text2 = outContent.toString(StandardCharsets.UTF_8);
        // допускаем отображение туза как 1 или 11 в зависимости от контекста подсчёта
        assertTrue(text2.contains("Карты дилера: [Туз Трефы (11), Тройка Бубны (3)] > 14")
                || text2.contains("Карты дилера: [Туз Трефы (1), Тройка Бубны (3)] > 14"));
    }
}
