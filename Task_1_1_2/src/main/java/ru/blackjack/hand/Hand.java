package ru.blackjack.hand;

import ru.blackjack.cards.Card;
import ru.blackjack.cards.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void add(Card card) {
        cards.add(card);
    }

    public void clear() {
        cards.clear();
    }

    public List<Card> viewCards() {
        return Collections.unmodifiableList(cards);
    }

    public int bestValue() {
        int sum = 0;
        int aces = 0;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                aces++;
                sum += 11;
            } else {
                sum += c.getRank().getBaseValue();
            }
        }
        while (sum > 21 && aces > 0) {
            sum -= 10; // один туз становится 1 вместо 11
            aces--;
        }
        return sum;
    }

    public boolean hasBlackjack() {
        return cards.size() == 2 && bestValue() == 21;
    }

    public boolean isBusted() {
        return bestValue() > 21;
    }


    public List<Integer> computePerCardValuesAsCurrentlyCounted() {
        int sum = 0;
        int totalAces = 0;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                totalAces++;
                sum += 11;
            } else {
                sum += c.getRank().getBaseValue();
            }
        }
        int acesCountedAsEleven = totalAces;
        while (sum > 21 && acesCountedAsEleven > 0) {
            sum -= 10;
            acesCountedAsEleven--;
        }

        List<Integer> values = new ArrayList<>(cards.size());
        int remainingAcesAs11 = acesCountedAsEleven;
        for (Card c : cards) {
            if (c.getRank().isAce()) {
                if (remainingAcesAs11 > 0) {
                    values.add(11);
                    remainingAcesAs11--;
                } else {
                    values.add(1);
                }
            } else {
                values.add(c.getRank().getBaseValue());
            }
        }
        return values;
    }
}
