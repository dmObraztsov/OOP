package ru.blackjack.cards;

/**
 * Масть игральной карты.
 */
public enum Suit {
    SPADES("Пики"),
    HEARTS("Червы"),
    CLUBS("Трефы"),
    DIAMONDS("Бубны");

    private final String russianName;

    /**
     * Создаёт масть с названием.
     *
     * @param russianName имя масти
     */
    Suit(String russianName) {
        this.russianName = russianName;
    }

    /**
     * Возвращает название масти.
     *
     * @return имя масти
     */
    public String getRussianName() {
        return russianName;
    }
}
