package br.com.delivery.application.dto;

import br.com.delivery.domain.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Schema(description = "Representa um pedido no sistema")
public class OrderDto {
    @Schema(description = "Identificador único do pedido", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "Identificador do cliente", example = "456e7890-e89b-12d3-a456-426614174001")
    private String customerId;
    
    @Schema(description = "Lista de itens do pedido")
    private List<OrderItemDto> items;
    
    @Schema(description = "Status atual do pedido", implementation = Order.OrderStatus.class)
    private Order.OrderStatus status;
    
    @Schema(description = "Data e hora de criação do pedido", example = "2025-01-27T10:30:00Z")
    private LocalDateTime createdAt;
    
    @Schema(description = "Valor total do pedido", example = "59.98")
    private BigDecimal total;

    public OrderDto() {}

    public OrderDto(String id, String customerId, List<OrderItemDto> items, Order.OrderStatus status, 
                   LocalDateTime createdAt, BigDecimal total) {
        this.id = id;
        this.customerId = customerId;
        this.items = items;
        this.status = status;
        this.createdAt = createdAt;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + items +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", total=" + total +
                '}';
    }
}
