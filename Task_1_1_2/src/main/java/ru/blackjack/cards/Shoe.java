package ru.blackjack.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class Shoe {

    private static final int RESHUFFLE_THRESHOLD = 15; // если осталось <=, пора перетасовать

    private final Random random = new Random();
    private final List<Card> cards = new ArrayList<>();

    public Shoe() {
        shuffleAllDecksBack();
    }


    public Card drawTopCard() {
        if (cards.isEmpty()) {
            shuffleAllDecksBack();
        }
        return cards.remove(cards.size() - 1);
    }

    public boolean shouldShuffleBecauseLowOnCards() {
        return cards.size() <= RESHUFFLE_THRESHOLD;
    }

    public void shuffleAllDecksBack() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards, random);
    }

}
