package br.com.delivery.application.dto;

import br.com.delivery.domain.entity.Order;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequest {
    @NotNull(message = "Status é obrigatório")
    private Order.OrderStatus status;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(Order.OrderStatus status) {
        this.status = status;
    }

    public Order.OrderStatus getStatus() {
        return status;
    }

    public void setStatus(Order.OrderStatus status) {
        this.status = status;
    }
}
