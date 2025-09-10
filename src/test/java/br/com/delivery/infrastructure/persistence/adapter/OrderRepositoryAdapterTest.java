package br.com.delivery.infrastructure.persistence.adapter;

import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.valueobject.OrderItem;
import br.com.delivery.infrastructure.persistence.entity.OrderEntity;
import br.com.delivery.infrastructure.persistence.repository.OrderJpaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do OrderRepositoryAdapter")
class OrderRepositoryAdapterTest {

    @Mock
    private OrderJpaRepository jpaRepository;

    @InjectMocks
    private OrderRepositoryAdapter orderRepositoryAdapter;

    private Order order;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        List<OrderItem> items = Arrays.asList(
            new OrderItem("product-1", 2, new BigDecimal("29.99")),
            new OrderItem("product-2", 1, new BigDecimal("15.50"))
        );
        
        order = new Order("order-123", "customer-456", items);
        
        orderEntity = new OrderEntity("order-123", "customer-456", Order.OrderStatus.CREATED, new BigDecimal("75.48"));
    }

    @Test
    @DisplayName("Deve salvar pedido com sucesso")
    void shouldSaveOrderSuccessfully() {
        // Given
        when(jpaRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        // When
        Order savedOrder = orderRepositoryAdapter.save(order);

        // Then
        assertNotNull(savedOrder);
        assertEquals("order-123", savedOrder.getId());
        assertEquals("customer-456", savedOrder.getCustomerId());
        assertEquals(Order.OrderStatus.CREATED, savedOrder.getStatus());

        verify(jpaRepository).save(any(OrderEntity.class));
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void shouldFindOrderByIdSuccessfully() {
        // Given
        when(jpaRepository.findById("order-123")).thenReturn(Optional.of(orderEntity));

        // When
        Optional<Order> result = orderRepositoryAdapter.findById("order-123");

        // Then
        assertTrue(result.isPresent());
        assertEquals("order-123", result.get().getId());
        assertEquals("customer-456", result.get().getCustomerId());

        verify(jpaRepository).findById("order-123");
    }

    @Test
    @DisplayName("Deve retornar vazio quando pedido não existe")
    void shouldReturnEmptyWhenOrderNotFound() {
        // Given
        when(jpaRepository.findById("order-999")).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderRepositoryAdapter.findById("order-999");

        // Then
        assertTrue(result.isEmpty());

        verify(jpaRepository).findById("order-999");
    }

    @Test
    @DisplayName("Deve listar todos os pedidos")
    void shouldFindAllOrders() {
        // Given
        List<OrderEntity> entities = Arrays.asList(orderEntity);
        when(jpaRepository.findAll()).thenReturn(entities);

        // When
        List<Order> result = orderRepositoryAdapter.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("order-123", result.get(0).getId());

        verify(jpaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar pedidos por ID do cliente")
    void shouldFindOrdersByCustomerId() {
        // Given
        List<OrderEntity> entities = Arrays.asList(orderEntity);
        when(jpaRepository.findByCustomerId("customer-456")).thenReturn(entities);

        // When
        List<Order> result = orderRepositoryAdapter.findByCustomerId("customer-456");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("customer-456", result.get(0).getCustomerId());

        verify(jpaRepository).findByCustomerId("customer-456");
    }

    @Test
    @DisplayName("Deve buscar pedidos por status")
    void shouldFindOrdersByStatus() {
        // Given
        List<OrderEntity> entities = Arrays.asList(orderEntity);
        when(jpaRepository.findByStatus(Order.OrderStatus.CREATED)).thenReturn(entities);

        // When
        List<Order> result = orderRepositoryAdapter.findByStatus(Order.OrderStatus.CREATED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.CREATED, result.get(0).getStatus());

        verify(jpaRepository).findByStatus(Order.OrderStatus.CREATED);
    }

    @Test
    @DisplayName("Deve deletar pedido por ID")
    void shouldDeleteOrderById() {
        // Given
        doNothing().when(jpaRepository).deleteById("order-123");

        // When
        orderRepositoryAdapter.deleteById("order-123");

        // Then
        verify(jpaRepository).deleteById("order-123");
    }

    @Test
    @DisplayName("Deve verificar se pedido existe")
    void shouldCheckIfOrderExists() {
        // Given
        when(jpaRepository.existsById("order-123")).thenReturn(true);

        // When
        boolean exists = orderRepositoryAdapter.existsById("order-123");

        // Then
        assertTrue(exists);

        verify(jpaRepository).existsById("order-123");
    }

    @Test
    @DisplayName("Deve converter entidade para domínio corretamente")
    void shouldConvertEntityToDomainCorrectly() {
        // Given
        when(jpaRepository.findById("order-123")).thenReturn(Optional.of(orderEntity));

        // When
        Optional<Order> result = orderRepositoryAdapter.findById("order-123");

        // Then
        assertTrue(result.isPresent());
        Order domainOrder = result.get();
        assertEquals("order-123", domainOrder.getId());
        assertEquals("customer-456", domainOrder.getCustomerId());
        assertEquals(Order.OrderStatus.CREATED, domainOrder.getStatus());
    }
}
