package ru.blackjack.cards;

public enum Rank {
    ACE("Туз", 11, true),
    TWO("Двойка", 2, false),
    THREE("Тройка", 3, false),
    FOUR("Четвёрка", 4, false),
    FIVE("Пятёрка", 5, false),
    SIX("Шестёрка", 6, false),
    SEVEN("Семёрка", 7, false),
    EIGHT("Восьмёрка", 8, false),
    NINE("Девятка", 9, false),
    TEN("Десятка", 10, false),
    JACK("Валет", 10, false),
    QUEEN("Дама", 10, false),
    KING("Король", 10, false);

    private final String russianName;
    private final int baseValue;
    private final boolean ace;

    Rank(String russianName, int baseValue, boolean ace) {
        this.russianName = russianName;
        this.baseValue = baseValue;
        this.ace = ace;
    }

    public String getRussianName() {
        return russianName;
    }

    public int getBaseValue() {
        return baseValue;
    }

    public boolean isAce() {
        return ace;
    }
}
