import model.PizzaOrder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    void printStatusWritesExpectedText() {
        PizzaOrder o = new PizzaOrder(7);
        o.setState(PizzaOrder.OrderState.IN_DELIVERY);

        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            o.printStatus();
        } finally {
            System.setOut(old);
        }

        String out = baos.toString();
        assertTrue(out.contains("[Заказ 7]"));
        assertTrue(out.contains(PizzaOrder.OrderState.IN_DELIVERY.getDescription()));
    }

    @Test
    void toStringContainsIdAndState() {
        PizzaOrder o = new PizzaOrder(99);
        o.setState(PizzaOrder.OrderState.IN_STORAGE);
        String s = o.toString();
        assertTrue(s.contains("orderId=99"));
        assertTrue(s.contains("IN_STORAGE"));
    }
}
