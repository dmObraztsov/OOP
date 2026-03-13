import config.PizzaShopConfig;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaShopConfigTest {

    private static String cfg(String name) {
        return Path.of("src", "test", "resources", "test-configs", name).toString();
    }

    @Test
    void loadFastConfig() {
        PizzaShopConfig c = PizzaShopConfig.loadFromJson(cfg("fast.json"));
        assertNotNull(c);
        assertEquals(2, c.bakers.size());
        assertEquals(1, c.couriers.size());
        assertEquals(20, c.warehouseCapacity);
        assertEquals(10_000, c.workingTime);
        assertEquals(1, c.ordersPerSecond);
        assertEquals(500, c.bakers.get(0).cookingTime);
        assertEquals(600, c.bakers.get(1).cookingTime);
    }

    @Test
    void loadWarehouseBottleneckConfig() {
        PizzaShopConfig c = PizzaShopConfig.loadFromJson(cfg("warehouse-bottleneck.json"));
        assertEquals(3, c.bakers.size());
        assertEquals(1, c.couriers.size());
        assertEquals(2, c.warehouseCapacity);
        assertEquals(30_000, c.workingTime);
        assertEquals(2, c.ordersPerSecond);
        assertEquals(5_000, c.couriers.get(0).deliveryTime);
    }

    @Test
    void loadStressConfig() {
        PizzaShopConfig c = PizzaShopConfig.loadFromJson(cfg("stress.json"));
        assertEquals(4, c.bakers.size());
        assertEquals(3, c.couriers.size());
        assertEquals(2, c.warehouseCapacity);
        assertEquals(10, c.ordersPerSecond);
        assertTrue(c.couriers.stream().allMatch(cc -> cc.bagCapacity == 1));
    }

    @Test
    void loadLongConfig() {
        PizzaShopConfig c = PizzaShopConfig.loadFromJson(cfg("long.json"));
        assertEquals(3, c.bakers.size());
        assertEquals(2, c.couriers.size());
        assertEquals(10, c.warehouseCapacity);
        assertEquals(120_000, c.workingTime);
        assertEquals(2, c.ordersPerSecond);
    }

    @Test
    void loadMissingFileThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> PizzaShopConfig.loadFromJson("no_such_file.json"));
        assertTrue(ex.getMessage().contains("Ошибка при загрузке конфигурации"));
    }
}
