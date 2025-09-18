package ru.blackjack.game;

import java.util.Objects;
import ru.blackjack.cards.Card;
import ru.blackjack.cards.Shoe;
import ru.blackjack.hand.Hand;


public class Participant {

    private final String displayName;
    private final Hand hand = new Hand();

    public Participant(String displayName) {
        this.displayName = Objects.requireNonNull(displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Hand getHand() {
        return hand;
    }

    public void resetHand() {
        hand.clear();
    }

    public void receiveCardFromShoe(Card card) {
        hand.add(card);
    }

    public void receiveCardFromShoe(Shoe shoe) {
        hand.add(shoe.drawTopCard());
    }

    protected void receiveFromShoeInternal(Shoe shoe) {
        hand.add(shoe.drawTopCard());
    }
}
