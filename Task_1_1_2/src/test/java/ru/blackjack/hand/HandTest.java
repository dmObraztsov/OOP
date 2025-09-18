package ru.blackjack.hand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;
import ru.blackjack.cards.Suit;
import java.util.List;

/**
 * Тесты класса {@link Hand}.
 * Проверяем подсчёт суммы, блэкджек, перебор и неизменяемый просмотр карт.
 */
class HandTest {

    /**
     * Проверяет, что две карты на 21 считаются блэкджеком.
     */
    @Test
    void blackjackWithTwoCardsIsDetected() {
        Hand h = new Hand();
        h.add(new Card(Suit.HEARTS, Rank.ACE));
        h.add(new Card(Suit.SPADES, Rank.KING));
        assertTrue(h.hasBlackjack());
        assertEquals(21, h.bestValue());
        assertFalse(h.isBusted());
    }

    /**
     * Проверяет понижение тузов с 11 до 1 для избежания перебора.
     */
    @Test
    void acesDowngradeFrom11To1ToAvoidBust() {
        Hand h = new Hand();
        h.add(new Card(Suit.CLUBS, Rank.ACE));
        h.add(new Card(Suit.DIAMONDS, Rank.ACE));
        h.add(new Card(Suit.SPADES, Rank.NINE));
        // 11 + 11 + 9 = 31 -> один туз становится 1: 21
        assertEquals(21, h.bestValue());

        List<Integer> perCard = h.computePerCardValuesAsCurrentlyCounted();
        assertEquals(3, perCard.size());
        // один туз как 11, второй как 1 (порядок соответствует добавлению)
        assertEquals(12, perCard.get(0) + perCard.get(1)); // 11+1 или 1+11
        assertEquals(9, perCard.get(2));
    }

    /**
     * Проверяет детектирование перебора.
     */
    @Test
    void bustDetected() {
        Hand h = new Hand();
        h.add(new Card(Suit.CLUBS, Rank.TEN));
        h.add(new Card(Suit.DIAMONDS, Rank.KING));
        h.add(new Card(Suit.HEARTS, Rank.TWO));
        assertTrue(h.isBusted());
        assertTrue(h.bestValue() > 21);
    }

    /**
     * Проверяет очистку руки и неизменяемость представления {@code viewCards()}.
     */
    @Test
    void clearAndViewCardsImmutability() {
        Hand h = new Hand();
        h.add(new Card(Suit.CLUBS, Rank.THREE));
        assertEquals(1, h.viewCards().size());
        assertThrows(UnsupportedOperationException.class, () ->
                h.viewCards().add(new Card(Suit.SPADES, Rank.FOUR)));
        h.clear();
        assertEquals(0, h.viewCards().size());
    }
}
