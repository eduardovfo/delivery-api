package br.com.delivery.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateOrderItemRequest {
    @NotBlank(message = "ID do produto é obrigatório")
    private String productId;

    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private int quantity;

    public CreateOrderItemRequest() {}

    public CreateOrderItemRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
