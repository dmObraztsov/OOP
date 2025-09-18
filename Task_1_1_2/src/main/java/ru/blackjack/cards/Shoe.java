package ru.blackjack.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Башмак с одной стандартной колодой из 52 карт. Умеет тасовать и выдавать верхнюю карту.
 */
public class Shoe {

    private static final int RESHUFFLE_THRESHOLD = 15; // если осталось <=, пора перетасовать

    private final Random random = new Random();
    private final List<Card> cards = new ArrayList<>();

    /**
     * Создаёт башмак и сразу перетасовывает колоду.
     */
    public Shoe() {
        shuffleAllDecksBack();
    }

    /**
     * Возвращает верхнюю карту из башмака.
     * Если карты закончились, колода автоматически перетасовывается и
     * берётся карта из новой колоды.
     *
     * @return снятая верхняя карта
     */
    public Card drawTopCard() {
        if (cards.isEmpty()) {
            shuffleAllDecksBack();
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Показывает, нужно ли перетасовать колоду из-за малого остатка.
     *
     * @return {@code true}, если карт осталось не больше порога; иначе {@code false}
     */
    public boolean shouldShuffleBecauseLowOnCards() {
        return cards.size() <= RESHUFFLE_THRESHOLD;
    }

    /**
     * Пересобирает и перетасовывает одну полную колоду из 52 карт.
     */
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
