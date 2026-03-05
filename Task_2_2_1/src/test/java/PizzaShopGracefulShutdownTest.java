import config.PizzaShopConfig;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaShopGracefulShutdownTest {

    private static PizzaShopConfig tinyConfig() {
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
        cfg.workingTime = 200; // short work time
        cfg.ordersPerSecond = 10;
        return cfg;
    }

    @Test
    void gracefulShutdownPrintsClosedAtEnd_noOrderLogsAfterClosed() throws Exception {
        PizzaShop shop = new PizzaShop(tinyConfig());

        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            Thread t = new Thread(shop::start, "PizzaShopTestMain");
            t.start();
            t.join(10_000);
            assertFalse(t.isAlive(), "pizza shop should finish quickly");
        } finally {
            System.setOut(oldOut);
        }

        String out = baos.toString();
        int closedIdx = out.lastIndexOf("=== ПИЦЦЕРИЯ ЗАКРЫТА ===");
        assertTrue(closedIdx >= 0, "should print closed marker");

        int lastOrderIdx = out.lastIndexOf("[Заказ");
        assertTrue(lastOrderIdx < closedIdx,
                "no order status lines should appear after 'ПИЦЦЕРИЯ ЗАКРЫТА'\nOutput was:\n" + out);
    }
}
