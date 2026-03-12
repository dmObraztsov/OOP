import config.PizzaShopConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PizzaShopFlowTest {

    @Test
    void shouldFinishWithoutDeadlock() {
        PizzaShopConfig config = new PizzaShopConfig();
        config.warehouseCapacity = 5;
        config.workingTime = 2000;
        config.ordersPerSecond = 10;

        PizzaShopConfig.BakerConfig baker = new PizzaShopConfig.BakerConfig();
        baker.id = 1;
        baker.cookingTime = 40;

        PizzaShopConfig.CourierConfig courier = new PizzaShopConfig.CourierConfig();
        courier.id = 1;
        courier.bagCapacity = 2;
        courier.deliveryTime = 50;

        config.bakers = java.util.List.of(baker);
        config.couriers = java.util.List.of(courier);

        assertDoesNotThrow(() -> new PizzaShop(config).start());
    }
}
