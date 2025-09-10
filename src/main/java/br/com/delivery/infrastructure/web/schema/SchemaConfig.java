package br.com.delivery.infrastructure.web.schema;

import br.com.delivery.domain.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaConfig {

    @Schema(
        name = "OrderStatus",
        description = "Status do pedido",
        enumAsRef = true
    )
    public enum OrderStatusSchema {
        @Schema(description = "Pedido criado, aguardando confirmação")
        CREATED,
        
        @Schema(description = "Pedido confirmado, em preparação")
        CONFIRMED,
        
        @Schema(description = "Pedido enviado para entrega")
        SHIPPED,
        
        @Schema(description = "Pedido entregue com sucesso")
        DELIVERED,
        
        @Schema(description = "Pedido cancelado")
        CANCELED
    }

    @Schema(
        name = "Customer",
        description = "Representa um cliente no sistema",
        example = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "name": "João Silva",
                "email": "joao@email.com",
                "document": "12345678901"
            }
            """
    )
    public static class CustomerSchema {}

    @Schema(
        name = "Product",
        description = "Representa um produto no sistema",
        example = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "name": "Notebook Dell Inspiron 15",
                "price": 2999.99
            }
            """
    )
    public static class ProductSchema {}

    @Schema(
        name = "Order",
        description = "Representa um pedido no sistema",
        example = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "customerId": "456e7890-e89b-12d3-a456-426614174001",
                "items": [
                    {
                        "productId": "789e0123-e89b-12d3-a456-426614174002",
                        "quantity": 2,
                        "unitPrice": 29.99
                    }
                ],
                "status": "CREATED",
                "createdAt": "2025-01-27T10:30:00Z",
                "total": 59.98
            }
            """
    )
    public static class OrderSchema {}

    @Schema(
        name = "OrderItem",
        description = "Representa um item de pedido",
        example = """
            {
                "productId": "789e0123-e89b-12d3-a456-426614174002",
                "quantity": 2,
                "unitPrice": 29.99
            }
            """
    )
    public static class OrderItemSchema {}

    @Schema(
        name = "CreateCustomerRequest",
        description = "Dados para criação de um novo cliente",
        example = """
            {
                "name": "João Silva",
                "email": "joao@email.com",
                "document": "12345678901"
            }
            """
    )
    public static class CreateCustomerRequestSchema {}

    @Schema(
        name = "CreateProductRequest",
        description = "Dados para criação de um novo produto",
        example = """
            {
                "name": "Notebook Dell Inspiron 15",
                "price": 2999.99
            }
            """
    )
    public static class CreateProductRequestSchema {}

    @Schema(
        name = "CreateOrderRequest",
        description = "Dados para criação de um novo pedido",
        example = """
            {
                "customerId": "456e7890-e89b-12d3-a456-426614174001",
                "items": [
                    {
                        "productId": "789e0123-e89b-12d3-a456-426614174002",
                        "quantity": 2
                    }
                ]
            }
            """
    )
    public static class CreateOrderRequestSchema {}

    @Schema(
        name = "CreateOrderItemRequest",
        description = "Dados para criação de um item de pedido",
        example = """
            {
                "productId": "789e0123-e89b-12d3-a456-426614174002",
                "quantity": 2
            }
            """
    )
    public static class CreateOrderItemRequestSchema {}

    @Schema(
        name = "UpdateOrderStatusRequest",
        description = "Dados para atualização do status de um pedido",
        example = """
            {
                "status": "CONFIRMED"
            }
            """
    )
    public static class UpdateOrderStatusRequestSchema {}

    @Schema(
        name = "PageResponse",
        description = "Resposta paginada genérica",
        example = """
            {
                "content": [
                    {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "name": "João Silva",
                        "email": "joao@email.com",
                        "document": "12345678901"
                    }
                ],
                "page": 0,
                "size": 20,
                "totalElements": 1,
                "totalPages": 1,
                "first": true,
                "last": true
            }
            """
    )
    public static class PageResponseSchema {}

    @Schema(
        name = "ProblemDetail",
        description = "Detalhes de erro seguindo RFC 7807",
        example = """
            {
                "type": "https://delivery-api.com/problems/validation-error",
                "title": "Validation Error",
                "status": 400,
                "detail": "One or more fields have validation errors",
                "instance": "/v1/customers",
                "timestamp": "2025-01-27T10:30:00Z",
                "extensions": {
                    "fieldErrors": {
                        "email": "Email deve ter formato válido",
                        "name": "Nome é obrigatório"
                    }
                }
            }
            """
    )
    public static class ProblemDetailSchema {}
}
