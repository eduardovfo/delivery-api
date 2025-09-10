package br.com.delivery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CreateOrderRequest {
    @NotBlank(message = "ID do cliente é obrigatório")
    private String customerId;

    @NotNull(message = "Itens são obrigatórios")
    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<CreateOrderItemRequest> items;

    public CreateOrderRequest() {}

    public CreateOrderRequest(String customerId, List<CreateOrderItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CreateOrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateOrderItemRequest> items) {
        this.items = items;
    }
}
