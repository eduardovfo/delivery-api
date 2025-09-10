package br.com.delivery.domain.entity;

import br.com.delivery.domain.valueobject.OrderItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da entidade Order")
class OrderTest {

    @Test
    @DisplayName("Deve criar pedido com dados válidos")
    void shouldCreateOrderWithValidData() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("29.99")),
            new OrderItem("product-2", 1, new BigDecimal("15.50"))
        );

        // When
        Order order = new Order(orderId, customerId, items);

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(customerId, order.getCustomerId());
        assertEquals(2, order.getItems().size());
        assertEquals(Order.OrderStatus.CREATED, order.getStatus());
        assertNotNull(order.getCreatedAt());
        assertEquals(new BigDecimal("75.48"), order.getTotal());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID é nulo")
    void shouldThrowExceptionWhenIdIsNull() {
        // Given
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("29.99"))
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(null, customerId, items)
        );
        assertEquals("Order ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID está vazio")
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Given
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("29.99"))
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order("   ", customerId, items)
        );
        assertEquals("Order ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando customerId é nulo")
    void shouldThrowExceptionWhenCustomerIdIsNull() {
        // Given
        String orderId = "order-123";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("29.99"))
        );

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(orderId, null, items)
        );
        assertEquals("Customer ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de itens é nula")
    void shouldThrowExceptionWhenItemsIsNull() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(orderId, customerId, null)
        );
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de itens está vazia")
    void shouldThrowExceptionWhenItemsIsEmpty() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Order(orderId, customerId, Collections.emptyList())
        );
        assertEquals("Order must have at least one item", exception.getMessage());
    }

    @Test
    @DisplayName("Deve calcular total corretamente")
    void shouldCalculateTotalCorrectly() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("10.00")), // 20.00
            new OrderItem("product-2", 3, new BigDecimal("15.50"))  // 46.50
        );

        // When
        Order order = new Order(orderId, customerId, items);

        // Then
        assertEquals(new BigDecimal("66.50"), order.getTotal());
    }

    @Test
    @DisplayName("Deve avançar status do pedido")
    void shouldAdvanceOrderStatus() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 1, new BigDecimal("10.00"))
        );
        Order order = new Order(orderId, customerId, items);

        // When
        order.advanceTo(Order.OrderStatus.CONFIRMED);

        // Then
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("Deve retornar lista imutável de itens")
    void shouldReturnImmutableItemsList() {
        // Given
        String orderId = "order-123";
        String customerId = "customer-456";
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 1, new BigDecimal("10.00"))
        );
        Order order = new Order(orderId, customerId, items);

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            order.getItems().add(new OrderItem("product-2", 1, new BigDecimal("5.00")));
        });
    }
}
