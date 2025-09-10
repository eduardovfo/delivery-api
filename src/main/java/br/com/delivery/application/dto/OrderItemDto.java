package br.com.delivery.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "Representa um item de pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderItemDto {
    @Schema(description = "Identificador do produto", example = "789e0123-e89b-12d3-a456-426614174002")
    private String productId;

    @Schema(description = "Quantidade do item", example = "2")
    private int quantity;

    @Schema(description = "Preço unitário do item", example = "29.99")
    private BigDecimal unitPrice;
}
