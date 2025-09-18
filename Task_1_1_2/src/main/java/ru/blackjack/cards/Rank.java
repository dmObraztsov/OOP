package ru.blackjack.cards;

/**
 * Достоинство (ранг) игральной карты.
 * Для туза базовое значение 11, но в подсчёте руки он может считаться как 1.
 */
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

    /**
     * Создаёт значение перечисления с локализованным именем и базовой оценкой.
     *
     * @param russianName название достоинства
     * @param baseValue базовое очко для карты (туз по умолчанию 11)
     * @param ace признак, что это туз
     */
    Rank(String russianName, int baseValue, boolean ace) {
        this.russianName = russianName;
        this.baseValue = baseValue;
        this.ace = ace;
    }

    /**
     * Возвращает название достоинства.
     *
     * @return имя
     */
    public String getRussianName() {
        return russianName;
    }

    /**
     * Возвращает базовое количество очков для карты.
     * Для туза это 11; в руке значение может понижаться до 1.
     *
     * @return базовое значение очков
     */
    public int getBaseValue() {
        return baseValue;
    }

    /**
     * Показывает, является ли данное достоинство тузом.
     *
     * @return {@code true}, если это туз; иначе {@code false}
     */
    public boolean isAce() {
        return ace;
    }
}
