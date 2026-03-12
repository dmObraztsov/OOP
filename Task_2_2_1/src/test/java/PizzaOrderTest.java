import model.PizzaOrder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaOrderTest {

    @Test
    void newOrderHasPendingState() {
        PizzaOrder o = new PizzaOrder(42);
        assertEquals(42, o.getOrderId());
        assertNotNull(o.getCreatedAt());
        assertEquals(PizzaOrder.OrderState.PENDING, o.getState());
    }

    @Test
    void setStateChangesState() {
        PizzaOrder o = new PizzaOrder(1);
        o.setState(PizzaOrder.OrderState.COOKING);
        assertEquals(PizzaOrder.OrderState.COOKING, o.getState());
    }

    @Test
    void toStringContainsIdAndDescription() {
        PizzaOrder o = new PizzaOrder(99);
        o.setState(PizzaOrder.OrderState.IN_STORAGE);
        String s = o.toString();
        assertTrue(s.contains("99"), "toString должен содержать ID заказа");
        assertTrue(s.contains(PizzaOrder.OrderState.IN_STORAGE.getDescription()), "toString должен содержать описание статуса");
    }
}
