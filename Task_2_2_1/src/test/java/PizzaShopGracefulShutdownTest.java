import config.PizzaShopConfig;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaShopGracefulShutdownTest {

    private PizzaShopConfig tinyConfig() {
        PizzaShopConfig cfg = new PizzaShopConfig();
        cfg.bakers = new ArrayList<>();
        PizzaShopConfig.BakerConfig b = new PizzaShopConfig.BakerConfig();
        b.id = 1;
        b.cookingTime = 5;
        cfg.bakers.add(b);

        cfg.couriers = new ArrayList<>();
        PizzaShopConfig.CourierConfig c = new PizzaShopConfig.CourierConfig();
        c.id = 1;
        c.bagCapacity = 2;
        c.deliveryTime = 5;
        cfg.couriers.add(c);

        cfg.warehouseCapacity = 5;
        cfg.workingTime = 200;
        cfg.ordersPerSecond = 10;
        return cfg;
    }

    @Test
    void gracefulShutdownPrintsClosedAtEnd() throws Exception {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            PizzaShop shop = new PizzaShop(tinyConfig());

            Thread t = new Thread(shop::start);
            t.start();

            t.join(3000);
            assertFalse(t.isAlive(), "Поток PizzaShop должен был завершиться сам");

            String out = baos.toString();
            assertTrue(out.contains("ПИЦЦЕРИЯ ЗАКРЫТА"),
                    "В консоли должна появиться фраза о закрытии. Весь вывод: " + out);

        } finally {
            System.setOut(oldOut);
        }
    }
}
