package ru.blackjack.ui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Тесты класса {@link RussianPhrases}.
 * Проверяем, что ключевые константы заданы и содержат ожидаемый текст.
 */
class RussianPhrasesTest {

    /**
     * Проверяет, что важные строковые константы не равны {@code null}
     * и приветствие содержит слово «Блэкджек».
     */
    @Test
    void constantsNotNull() {
        assertNotNull(RussianPhrases.WELCOME);
        assertNotNull(RussianPhrases.USING_ONE_DECK);
        assertNotNull(RussianPhrases.ASK_HIT_OR_STAND);
        assertTrue(RussianPhrases.WELCOME.contains("Блэкджек"));
    }
}
