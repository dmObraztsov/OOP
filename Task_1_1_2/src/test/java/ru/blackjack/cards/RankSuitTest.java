package ru.blackjack.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankSuitTest {

    @Test
    void suitRussianNames() {
        assertEquals("Пики", Suit.SPADES.getRussianName());
        assertEquals("Червы", Suit.HEARTS.getRussianName());
        assertEquals("Трефы", Suit.CLUBS.getRussianName());
        assertEquals("Бубны", Suit.DIAMONDS.getRussianName());
    }

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
