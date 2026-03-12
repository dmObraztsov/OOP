package model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

public class PizzaOrder {
    private final int orderId;
    private final LocalDateTime createdAt;
    private final AtomicReference<OrderState> state;

    public enum OrderState {
        PENDING("Заказ создан, ожидает пекаря"),
        COOKING("Пицца готовится"),
        WAITING_FOR_STORAGE("Ожидание свободного места на складе"),
        IN_STORAGE("На складе"),
        IN_DELIVERY("На доставке"),
        DELIVERED("Доставлено");

        private final String description;

        OrderState(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public PizzaOrder(int orderId) {
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
        this.state = new AtomicReference<>(OrderState.PENDING);
    }

    public int getOrderId() {
        return orderId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public synchronized OrderState getState() {
        return state.get();
    }

    public synchronized void setState(OrderState newState) {
        state.set(newState);
    }

    @Override
    public String toString() {
        return String.format("[%s] Заказ #%d: %s", createdAt.toLocalTime().withNano(0), orderId, state.get().getDescription());
    }
}
