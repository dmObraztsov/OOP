package ru.blackjack.cards;

import java.util.Objects;

/**
 * Карта игральной колоды.
 */
public final class Card {

    private final Suit suit;
    private final Rank rank;

    /**
     * Создаёт карту с указанными мастью и достоинством.
     *
     * @param suit масть карты
     * @param rank достоинство карты
     */
    public Card(Suit suit, Rank rank) {
        this.suit = Objects.requireNonNull(suit);
        this.rank = Objects.requireNonNull(rank);
    }

    /**
     * Возвращает масть карты.
     *
     * @return масть
     */
    public Suit getSuit() {
        return suit;
    }

    /**
     * Возвращает достоинство карты.
     *
     * @return достоинство
     */
    public Rank getRank() {
        return rank;
    }

    /**
     * Строит отображаемое имя карты без значения очков.
     *
     * @return строка вида «Дама Пики»
     */
    public String buildDisplayNameWithoutValue() {
        return rank.getRussianName() + " " + suit.getRussianName();
    }
}
