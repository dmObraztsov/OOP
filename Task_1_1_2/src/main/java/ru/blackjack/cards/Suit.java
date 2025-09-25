package ru.blackjack.cards;


public enum Suit {
    SPADES("Пики"),
    HEARTS("Червы"),
    CLUBS("Трефы"),
    DIAMONDS("Бубны");

    private final String russianName;

    Suit(String russianName) {
        this.russianName = russianName;
    }

    public String getRussianName() {
        return russianName;
    }
}
