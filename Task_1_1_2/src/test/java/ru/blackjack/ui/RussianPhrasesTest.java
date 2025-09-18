package ru.blackjack.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RussianPhrasesTest {

    @Test
    void constantsNotNull() {
        assertNotNull(RussianPhrases.WELCOME);
        assertNotNull(RussianPhrases.USING_ONE_DECK);
        assertNotNull(RussianPhrases.ASK_HIT_OR_STAND);
        assertTrue(RussianPhrases.WELCOME.contains("Блэкджек"));
    }
}
