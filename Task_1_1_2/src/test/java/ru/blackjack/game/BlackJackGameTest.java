package ru.blackjack.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;
import ru.blackjack.cards.Shoe;
import ru.blackjack.cards.Suit;
import ru.blackjack.ui.ConsoleUserIo;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;


/**
 * Тестируем раунд через подмену Shoe (рефлексией) и заглушку IO.
 */
class BlackJackGameTest {

    /**
     * Фиксированный башмак: отдаёт карты из очереди, не перетасовывается.
     */
    static class FixedShoe extends Shoe {
        private final Deque<Card> queue = new ArrayDeque<>();

        FixedShoe(Deque<Card> initial) {
            super(); // создаст обычную колоду, но мы переопределим поведение
            this.queue.addAll(initial);
        }

        @Override
        public Card drawTopCard() {
            return queue.removeFirst();
        }

        @Override
        public boolean shouldShuffleBecauseLowOnCards() {
            return false;
        }

        @Override
        public void shuffleAllDecksBack() {
            // ничего — фиксированная последовательность
        }
    }

    /**
     * Заглушка IO: всегда не хочу играть ещё.
     */
    static class StubIo extends ConsoleUserIo {
        private final StringBuilder buffer = new StringBuilder();

        public StubIo() {
            super(new Scanner(new ByteArrayInputStream(new byte[0]), StandardCharsets.UTF_8));
        }

        @Override
        public int askUserHitOrStand() {
            return 0;
        } // всегда стою

        @Override
        public boolean askUserWhetherPlayAnotherRound() {
            return false;
        } // 1 раунд
    }

    @Test
    void playerWinsWhenDealerBusts() throws Exception {
        // Очередность вытягивания карт в игре:
        // P1, Dopen, P2, Dhole, далее карты дилера.

        Deque<Card> seq = new ArrayDeque<>();
        // Игрок: 10 + 7 = 17, стоит
        seq.add(new Card(Suit.SPADES, Rank.QUEEN)); // P1
        seq.add(new Card(Suit.CLUBS, Rank.ACE));    // Dopen (туз)
        seq.add(new Card(Suit.SPADES, Rank.SEVEN)); // P2
        seq.add(new Card(Suit.CLUBS, Rank.THREE));  // Dhole -> у дилера 14 (11+3=14)

        // Дилер добирает: 10 -> 24 (туз станет 1, итого 14), ещё 10 -> 24 перебор
        seq.add(new Card(Suit.SPADES, Rank.TEN));   // dealer draw #1
        seq.add(new Card(Suit.DIAMONDS, Rank.KING));// dealer draw #2 -> bust

        FixedShoe fixed = new FixedShoe(seq);

        StubIo io = new StubIo();
        BlackJackGame game = new BlackJackGame(io);

        // Рефлексией подменим приватное поле shoe
        Field shoeField = BlackJackGame.class.getDeclaredField("shoe");
        shoeField.setAccessible(true);
        shoeField.set(game, fixed);

        // И запускаем цикл — он сыграет один раунд и выйдет
        // (askUserWhetherPlayAnotherRound() -> false)
        game.startGameLoopUntilUserStops();

        Field pw = BlackJackGame.class.getDeclaredField("playerWins");
        Field dw = BlackJackGame.class.getDeclaredField("dealerWins");
        pw.setAccessible(true);
        dw.setAccessible(true);
        int playerWins = pw.getInt(game);
        int dealerWins = dw.getInt(game);
        assertEquals(1, playerWins);
        assertEquals(0, dealerWins);
    }

    @Test
    void blackjackImmediatelyResolves() throws Exception {
        // Игрок получает Blackjack (Туз + Король), дилер — нет.
        Deque<Card> seq = new ArrayDeque<>();
        seq.add(new Card(Suit.HEARTS, Rank.ACE));     // P1
        seq.add(new Card(Suit.SPADES, Rank.SEVEN));   // Dopen
        seq.add(new Card(Suit.DIAMONDS, Rank.KING));  // P2 -> 21
        seq.add(new Card(Suit.CLUBS, Rank.THREE));    // Dhole (не важно)

        FixedShoe fixed = new FixedShoe(seq);
        StubIo io = new StubIo();
        BlackJackGame game = new BlackJackGame(io);

        Field shoeField = BlackJackGame.class.getDeclaredField("shoe");
        shoeField.setAccessible(true);
        shoeField.set(game, fixed);

        game.startGameLoopUntilUserStops();

        Field pw = BlackJackGame.class.getDeclaredField("playerWins");
        Field dw = BlackJackGame.class.getDeclaredField("dealerWins");
        pw.setAccessible(true);
        dw.setAccessible(true);
        assertEquals(1, pw.getInt(game));
        assertEquals(0, dw.getInt(game));
    }
}
