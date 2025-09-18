package ru.blackjack.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * Тесты перечислений {@link Suit} и {@link Rank}.
 * Проверяем локализованные названия мастей и базовые значения рангов.
 */
class RankSuitTest {

    /**
     * Проверяет русские названия мастей.
     */
    @Test
    void suitRussianNames() {
        assertEquals("Пики", Suit.SPADES.getRussianName());
        assertEquals("Червы", Suit.HEARTS.getRussianName());
        assertEquals("Трефы", Suit.CLUBS.getRussianName());
        assertEquals("Бубны", Suit.DIAMONDS.getRussianName());
    }

    /**
     * Проверяет базовые значения рангов и флаг туза.
     */
    @Test
    void ranksBaseValuesAndAceFlag() {
        assertEquals(11, Rank.ACE.getBaseValue());
        assertTrue(Rank.ACE.isAce());

        assertEquals(10, Rank.TEN.getBaseValue());
        assertFalse(Rank.TEN.isAce());

        assertEquals(10, Rank.KING.getBaseValue());
        assertEquals(10, Rank.QUEEN.getBaseValue());
        assertEquals(10, Rank.JACK.getBaseValue());
    }
}
