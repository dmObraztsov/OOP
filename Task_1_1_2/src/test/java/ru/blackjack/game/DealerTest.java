package ru.blackjack.game;

import org.junit.jupiter.api.Test;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;
import ru.blackjack.cards.Suit;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void holeCardLifecycle() {
        Dealer d = new Dealer("Дилер");

        d.resetHandAndHideHoleCard();
        assertFalse(d.isHoleCardHidden());
        assertNull(d.getHoleCardIfAny());

        d.receiveOpenCardFromShoe(new Card(Suit.SPADES, Rank.SEVEN));
        d.receiveHoleCardFromShoe(new Card(Suit.HEARTS, Rank.ACE));
        assertTrue(d.isHoleCardHidden());
        assertNotNull(d.getHoleCardIfAny());
        assertEquals(1, d.getHand().viewCards().size());

        d.revealHoleCard();
        assertFalse(d.isHoleCardHidden());
        assertEquals(2, d.getHand().viewCards().size());
    }

    @Test
    void mustDrawAccordingToRules_Until17() {
        Dealer d = new Dealer("Дилер");
        d.resetHandAndHideHoleCard();

        d.receiveOpenCardFromShoe(new Card(Suit.CLUBS, Rank.SIX));
        d.receiveOpenCardFromShoe(new Card(Suit.DIAMONDS, Rank.NINE)); // 15
        assertTrue(d.mustDrawAccordingToRules());

        d.receiveOpenCardFromShoe(new Card(Suit.SPADES, Rank.TWO)); // 17
        assertFalse(d.mustDrawAccordingToRules());
    }
}
