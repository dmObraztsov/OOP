package ru.blackjack.cards;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class ShoeTest {

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
