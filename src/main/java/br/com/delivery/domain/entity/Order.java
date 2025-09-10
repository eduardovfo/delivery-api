package br.com.delivery.domain.entity;

import br.com.delivery.domain.valueobject.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public Order(String id, String customerId, List<OrderItem> items) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        
        this.id = id.trim();
        this.customerId = customerId.trim();
        this.items = new ArrayList<>(items);
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void advanceTo(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        
        if (status == OrderStatus.CANCELED) {
            throw new IllegalStateException("Cannot change status of a canceled order");
        }
        
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot change status of a delivered order");
        }
        
        if (newStatus == OrderStatus.CREATED) {
            throw new IllegalArgumentException("Cannot revert to CREATED status");
        }
        
        this.status = newStatus;
    }

    public void cancel() {
        if (status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        this.status = OrderStatus.CANCELED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + items +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    @io.swagger.v3.oas.annotations.media.Schema(
        description = "Status do pedido",
        enumAsRef = true
    )
    public enum OrderStatus {
        @io.swagger.v3.oas.annotations.media.Schema(description = "Pedido criado, aguardando confirmação")
        CREATED,
        
        @io.swagger.v3.oas.annotations.media.Schema(description = "Pedido confirmado, em preparação")
        CONFIRMED,
        
        @io.swagger.v3.oas.annotations.media.Schema(description = "Pedido enviado para entrega")
        SHIPPED,
        
        @io.swagger.v3.oas.annotations.media.Schema(description = "Pedido entregue com sucesso")
        DELIVERED,
        
        @io.swagger.v3.oas.annotations.media.Schema(description = "Pedido cancelado")
        CANCELED
    }
}
