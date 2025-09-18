package ru.blackjack.cards;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Тесты башмака {@link Shoe}.
 * Проверяем уникальность 52 карт и логику порога перетасовки.
 */
class ShoeTest {

    /**
     * Убеждаемся, что одна колода даёт 52 уникальные карты,
     * а затем можно продолжать тянуть карты (после автоматической перетасовки).
     */
    @Test
    void oneDeckHas52UniqueCardsAndKeepsDrawing() {
        Shoe shoe = new Shoe();

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            Card c = shoe.drawTopCard();
            assertNotNull(c);
            String key = c.getSuit() + "-" + c.getRank();
            assertTrue(seen.add(key), "duplicate card: " + key);
        }

        // 53-я карта должна прийти после автоматической перетасовки
        Card extra = shoe.drawTopCard();
        assertNotNull(extra);
    }

    /**
     * Проверяем, что триггер «мало карт — нужно перетасовать» срабатывает у конца колоды.
     */
    @Test
    void shouldShuffleThresholdTriggersNearEnd() {
        Shoe shoe = new Shoe();
        // Вытащим почти всю колоду
        for (int i = 0; i < 52 - 15; i++) {
            shoe.drawTopCard();
        }
        assertTrue(shoe.shouldShuffleBecauseLowOnCards());
    }
}
