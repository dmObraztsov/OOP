package ru.blackjack.cards;

import java.util.Objects;


public final class Card {

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = Objects.requireNonNull(suit);
        this.rank = Objects.requireNonNull(rank);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String buildDisplayNameWithoutValue() {
        return rank.getRussianName() + " " + suit.getRussianName();
    }
}
