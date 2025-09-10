package br.com.delivery.application.dto;

import br.com.delivery.domain.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Representa um pedido no sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
}
