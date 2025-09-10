package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CreateOrderRequest;
import br.com.delivery.application.dto.CreateOrderItemRequest;
import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.mapper.OrderMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import br.com.delivery.domain.port.OrderRepositoryPort;
import br.com.delivery.domain.port.ProductRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CreateOrderUseCase")
class CreateOrderUseCaseTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private ProductRepositoryPort productRepository;

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private Customer customer;
    private Product product;
    private CreateOrderRequest request;

    @BeforeEach
    void setUp() {
        customer = new Customer("customer-123", "João Silva", "joao@email.com", "12345678901");
        product = new Product("product-456", "Pizza Margherita", new BigDecimal("29.99"));
        
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId("product-456");
        itemRequest.setQuantity(2);
        
        request = new CreateOrderRequest();
        request.setCustomerId("customer-123");
        request.setItems(Arrays.asList(itemRequest));
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void shouldCreateOrderSuccessfully() {
        // Given
        Order savedOrder = new Order("order-789", "customer-123", 
            Arrays.asList(new br.com.delivery.domain.valueobject.OrderItem("product-456", 2, new BigDecimal("29.99"))));
        
        OrderDto expectedDto = new OrderDto();
        expectedDto.setId("order-789");
        expectedDto.setCustomerId("customer-123");
        expectedDto.setTotal(new BigDecimal("59.98"));

        when(customerRepository.findById("customer-123")).thenReturn(Optional.of(customer));
        when(productRepository.findById("product-456")).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(expectedDto);

        // When
        OrderDto result = createOrderUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals("order-789", result.getId());
        assertEquals("customer-123", result.getCustomerId());
        assertEquals(new BigDecimal("59.98"), result.getTotal());

        verify(customerRepository).findById("customer-123");
        verify(productRepository).findById("product-456");
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDto(savedOrder);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não existe")
    void shouldThrowExceptionWhenCustomerNotFound() {
        // Given
        when(customerRepository.findById("customer-123")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Cliente não encontrado", exception.getMessage());

        verify(customerRepository).findById("customer-123");
        verify(productRepository, never()).findById(anyString());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não existe")
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(customerRepository.findById("customer-123")).thenReturn(Optional.of(customer));
        when(productRepository.findById("product-456")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> createOrderUseCase.execute(request)
        );
        assertEquals("Produto não encontrado: product-456", exception.getMessage());

        verify(customerRepository).findById("customer-123");
        verify(productRepository).findById("product-456");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve criar pedido com múltiplos itens")
    void shouldCreateOrderWithMultipleItems() {
        // Given
        Product product2 = new Product("product-789", "Coca-Cola", new BigDecimal("5.50"));
        
        CreateOrderItemRequest itemRequest2 = new CreateOrderItemRequest();
        itemRequest2.setProductId("product-789");
        itemRequest2.setQuantity(3);
        
        request.setItems(Arrays.asList(
            request.getItems().get(0),
            itemRequest2
        ));

        Order savedOrder = new Order("order-789", "customer-123", 
            Arrays.asList(
                new br.com.delivery.domain.valueobject.OrderItem("product-456", 2, new BigDecimal("29.99")),
                new br.com.delivery.domain.valueobject.OrderItem("product-789", 3, new BigDecimal("5.50"))
            ));
        
        OrderDto expectedDto = new OrderDto();
        expectedDto.setId("order-789");
        expectedDto.setTotal(new BigDecimal("74.48"));

        when(customerRepository.findById("customer-123")).thenReturn(Optional.of(customer));
        when(productRepository.findById("product-456")).thenReturn(Optional.of(product));
        when(productRepository.findById("product-789")).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toDto(savedOrder)).thenReturn(expectedDto);

        // When
        OrderDto result = createOrderUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("74.48"), result.getTotal());

        verify(customerRepository).findById("customer-123");
        verify(productRepository).findById("product-456");
        verify(productRepository).findById("product-789");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve usar preço atual do produto")
    void shouldUseCurrentProductPrice() {
        // Given
        Product productWithUpdatedPrice = new Product("product-456", "Pizza Margherita", new BigDecimal("35.99"));
        when(customerRepository.findById("customer-123")).thenReturn(Optional.of(customer));
        when(productRepository.findById("product-456")).thenReturn(Optional.of(productWithUpdatedPrice));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return order; // Retorna o mesmo pedido para verificar o preço
        });
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        // When
        createOrderUseCase.execute(request);

        // Then
        verify(orderRepository).save(argThat(order -> 
            order.getItems().get(0).getUnitPrice().equals(new BigDecimal("35.99"))
        ));
    }
}
