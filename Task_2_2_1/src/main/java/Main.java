import config.PizzaShopConfig;
import logger.Logger;

public class Main {
    public static void main(String[] args) {
        try {
            PizzaShopConfig config = PizzaShopConfig.loadFromJson("src/main/resources/config.json");
            new PizzaShop(config).start();
        } catch (Exception e) {
            Logger.error("Запуск невозможен: " + e.getMessage());
        }
    }
}
