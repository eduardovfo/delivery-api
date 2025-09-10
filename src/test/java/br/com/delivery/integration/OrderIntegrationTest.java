package br.com.delivery.integration;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CreateOrderRequest;
import br.com.delivery.application.dto.CreateOrderItemRequest;
import br.com.delivery.application.dto.CreateProductRequest;
import br.com.delivery.application.usecase.CreateCustomerUseCase;
import br.com.delivery.application.usecase.CreateOrderUseCase;
import br.com.delivery.application.usecase.CreateProductUseCase;
import br.com.delivery.application.usecase.GetOrderUseCase;
import br.com.delivery.domain.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração - Fluxo Completo de Pedidos")
class OrderIntegrationTest {

    @Autowired
    private CreateCustomerUseCase createCustomerUseCase;

    @Autowired
    private CreateProductUseCase createProductUseCase;

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private GetOrderUseCase getOrderUseCase;

    private String customerId;
    private String productId;

    @BeforeEach
    void setUp() {
        // Criar cliente
        CreateCustomerRequest customerRequest = new CreateCustomerRequest();
        customerRequest.setName("João Silva");
        customerRequest.setEmail("joao@email.com");
        customerRequest.setDocument("12345678901");
        customerId = createCustomerUseCase.execute(customerRequest).getId();

        // Criar produto
        CreateProductRequest productRequest = new CreateProductRequest();
        productRequest.setName("Pizza Margherita");
        productRequest.setPrice(new BigDecimal("29.99"));
        productId = createProductUseCase.execute(productRequest).getId();
    }

    @Test
    @DisplayName("Deve criar e buscar pedido com sucesso")
    void shouldCreateAndRetrieveOrderSuccessfully() {
        // Given
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(2);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setItems(Arrays.asList(itemRequest));

        // When
        var createdOrder = createOrderUseCase.execute(orderRequest);
        var retrievedOrder = getOrderUseCase.execute(createdOrder.getId()).orElse(null);

        // Then
        assertNotNull(createdOrder);
        assertNotNull(retrievedOrder);
        assertEquals(createdOrder.getId(), retrievedOrder.getId());
        assertEquals(customerId, retrievedOrder.getCustomerId());
        assertEquals(Order.OrderStatus.CREATED, retrievedOrder.getStatus());
        assertEquals(new BigDecimal("59.98"), retrievedOrder.getTotal());
        assertNotNull(retrievedOrder.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar pedido com múltiplos itens")
    void shouldCreateOrderWithMultipleItems() {
        // Given
        CreateProductRequest productRequest2 = new CreateProductRequest();
        productRequest2.setName("Coca-Cola");
        productRequest2.setPrice(new BigDecimal("5.50"));
        String productId2 = createProductUseCase.execute(productRequest2).getId();

        CreateOrderItemRequest itemRequest1 = new CreateOrderItemRequest();
        itemRequest1.setProductId(productId);
        itemRequest1.setQuantity(2);

        CreateOrderItemRequest itemRequest2 = new CreateOrderItemRequest();
        itemRequest2.setProductId(productId2);
        itemRequest2.setQuantity(3);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setItems(Arrays.asList(itemRequest1, itemRequest2));

        // When
        var createdOrder = createOrderUseCase.execute(orderRequest);

        // Then
        assertNotNull(createdOrder);
        assertEquals(2, createdOrder.getItems().size());
        assertEquals(new BigDecimal("74.48"), createdOrder.getTotal());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId(productId);
        itemRequest.setQuantity(2);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId("customer-inexistente");
        orderRequest.setItems(Arrays.asList(itemRequest));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createOrderUseCase.execute(orderRequest)
        );
        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não existe")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId("product-inexistente");
        itemRequest.setQuantity(2);

        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setItems(Arrays.asList(itemRequest));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createOrderUseCase.execute(orderRequest)
        );
        assertEquals("Produto não encontrado: product-inexistente", exception.getMessage());
    }
}
