package config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PizzaShopConfig {

    public static class BakerConfig {
        public int id;
        public long cookingTime;
    }

    public static class CourierConfig {
        public int id;
        public int bagCapacity;
        public long deliveryTime;
    }

    public List<BakerConfig> bakers;
    public List<CourierConfig> couriers;
    public int warehouseCapacity;
    public long workingTime;
    public int ordersPerSecond;

    public static PizzaShopConfig loadFromJson(String filePath) {
        try {
            StringBuilder jsonContent = new StringBuilder();
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
                int ch;
                while ((ch = reader.read()) != -1) {
                    jsonContent.append((char) ch);
                }
            }

            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONObject bakery = jsonObject.getJSONObject("bakeries");

            PizzaShopConfig config = new PizzaShopConfig();

            config.bakers = new ArrayList<>();
            JSONArray bakersArray = bakery.getJSONArray("bakers");
            for (int i = 0; i < bakersArray.length(); i++) {
                JSONObject bakerJson = bakersArray.getJSONObject(i);
                BakerConfig bakerConfig = new BakerConfig();
                bakerConfig.id = bakerJson.getInt("id");
                bakerConfig.cookingTime = bakerJson.getLong("cookingTime");
                config.bakers.add(bakerConfig);
            }

            config.couriers = new ArrayList<>();
            JSONArray couriersArray = bakery.getJSONArray("couriers");
            for (int i = 0; i < couriersArray.length(); i++) {
                JSONObject courierJson = couriersArray.getJSONObject(i);
                CourierConfig courierConfig = new CourierConfig();
                courierConfig.id = courierJson.getInt("id");
                courierConfig.bagCapacity = courierJson.getInt("bagCapacity");
                courierConfig.deliveryTime = courierJson.getLong("deliveryTime");
                config.couriers.add(courierConfig);
            }

            JSONObject warehouse = bakery.getJSONObject("warehouse");
            config.warehouseCapacity = warehouse.getInt("capacity");
            config.workingTime = bakery.getLong("workingTime");
            config.ordersPerSecond = bakery.getInt("ordersPerSecond");

            return config;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке конфигурации: " + e.getMessage(), e);
        }
    }
}
