package com.example.task_2_3_1.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameConfigTest {
    @Test
    void testConfigInitialization() {
        assertAll("Конфигурация игры",
                () -> assertTrue(GameConfig.TILE_SIZE > 0),
                () -> assertNotNull(GameConfig.COLOR_SNAKE_HEAD),
                () -> assertNotNull(GameConfig.COLOR_BG_DARK),
                () -> assertEquals(10, GameConfig.TARGET_LENGTH),
                () -> assertFalse(GameConfig.FONT_FAMILY.isEmpty())
        );
    }
}