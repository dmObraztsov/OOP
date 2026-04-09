package com.example.task_2_3_1.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameConfigTest {
    @Test
    void testConfigValues() {
        assertTrue(GameConfig.TILE_SIZE > 0);
        assertNotNull(GameConfig.COLOR_BG_LIGHT);
        assertEquals("Arial", GameConfig.FONT_FAMILY);
    }
}