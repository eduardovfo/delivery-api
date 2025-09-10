package br.com.delivery.domain.valueobject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do value object OrderItem")
class OrderItemTest {

    @Test
    @DisplayName("Deve criar item com dados válidos")
    void shouldCreateItemWithValidData() {
        // Given
        String productId = "product-123";
        int quantity = 2;
        BigDecimal unitPrice = new BigDecimal("29.99");

        // When
        OrderItem item = new OrderItem(productId, quantity, unitPrice);

        // Then
        assertNotNull(item);
        assertEquals(productId, item.getProductId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
        assertEquals(new BigDecimal("59.98"), item.getTotalPrice());
    }

    @Test
    @DisplayName("Deve lançar exceção quando productId é nulo")
    void shouldThrowExceptionWhenProductIdIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem(null, 2, new BigDecimal("29.99"))
        );
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando productId está vazio")
    void shouldThrowExceptionWhenProductIdIsEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("   ", 2, new BigDecimal("29.99"))
        );
        assertEquals("Product ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade é menor que 1")
    void shouldThrowExceptionWhenQuantityIsLessThanOne() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("product-123", 0, new BigDecimal("29.99"))
        );
        assertEquals("Quantity must be at least 1", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é nulo")
    void shouldThrowExceptionWhenUnitPriceIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("product-123", 2, null)
        );
        assertEquals("Unit price must be non-negative", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando preço unitário é negativo")
    void shouldThrowExceptionWhenUnitPriceIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new OrderItem("product-123", 2, new BigDecimal("-10.00"))
        );
        assertEquals("Unit price must be non-negative", exception.getMessage());
    }

    @Test
    @DisplayName("Deve aceitar preço unitário zero")
    void shouldAcceptZeroUnitPrice() {
        // Given
        String productId = "product-123";
        int quantity = 2;
        BigDecimal unitPrice = BigDecimal.ZERO;

        // When
        OrderItem item = new OrderItem(productId, quantity, unitPrice);

        // Then
        assertNotNull(item);
        assertEquals(BigDecimal.ZERO, item.getTotalPrice());
    }

    @Test
    @DisplayName("Deve calcular preço total corretamente")
    void shouldCalculateTotalPriceCorrectly() {
        // Given
        String productId = "product-123";
        int quantity = 3;
        BigDecimal unitPrice = new BigDecimal("12.50");

        // When
        OrderItem item = new OrderItem(productId, quantity, unitPrice);

        // Then
        assertEquals(new BigDecimal("37.50"), item.getTotalPrice());
    }

    @Test
    @DisplayName("Deve remover espaços em branco do productId")
    void shouldTrimProductId() {
        // Given
        String productId = "  product-123  ";
        int quantity = 2;
        BigDecimal unitPrice = new BigDecimal("29.99");

        // When
        OrderItem item = new OrderItem(productId, quantity, unitPrice);

        // Then
        assertEquals("product-123", item.getProductId());
    }

    @Test
    @DisplayName("Deve ser igual a outro item com mesmos dados")
    void shouldBeEqualWithSameData() {
        // Given
        OrderItem item1 = new OrderItem("product-123", 2, new BigDecimal("29.99"));
        OrderItem item2 = new OrderItem("product-123", 2, new BigDecimal("29.99"));

        // When & Then
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    @DisplayName("Deve ser diferente de item com dados diferentes")
    void shouldNotBeEqualWithDifferentData() {
        // Given
        OrderItem item1 = new OrderItem("product-123", 2, new BigDecimal("29.99"));
        OrderItem item2 = new OrderItem("product-456", 2, new BigDecimal("29.99"));

        // When & Then
        assertNotEquals(item1, item2);
    }
}
