package ru.blackjack.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 *
 */
class CardTest {

    @Test
    void buildDisplayNameWithoutValue_russianFormat() {
        Card c = new Card(Suit.SPADES, Rank.QUEEN);
        assertEquals("Дама Пики", c.buildDisplayNameWithoutValue());
    }

    @Test
    void gettersWork() {
        Card c = new Card(Suit.CLUBS, Rank.ACE);
        assertEquals(Suit.CLUBS, c.getSuit());
        assertEquals(Rank.ACE, c.getRank());
        assertTrue(c.getRank().isAce());
        assertEquals(11, c.getRank().getBaseValue());
    }
}
