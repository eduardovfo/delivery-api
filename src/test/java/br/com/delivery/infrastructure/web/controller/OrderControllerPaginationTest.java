package br.com.delivery.infrastructure.web.controller;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.usecase.ListOrdersUseCase;
import br.com.delivery.domain.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerPaginationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListOrdersUseCase listOrdersUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    private List<OrderDto> orders;

    @BeforeEach
    void setUp() {
        // Criar lista de pedidos para teste
        OrderDto order1 = new OrderDto();
        order1.setId("order-1");
        order1.setCustomerId("customer-1");
        order1.setStatus(Order.OrderStatus.CONFIRMED);
        order1.setTotal(BigDecimal.valueOf(100.00));
        order1.setCreatedAt(LocalDateTime.now());

        OrderDto order2 = new OrderDto();
        order2.setId("order-2");
        order2.setCustomerId("customer-2");
        order2.setStatus(Order.OrderStatus.CONFIRMED);
        order2.setTotal(BigDecimal.valueOf(200.00));
        order2.setCreatedAt(LocalDateTime.now());

        OrderDto order3 = new OrderDto();
        order3.setId("order-3");
        order3.setCustomerId("customer-3");
        order3.setStatus(Order.OrderStatus.CREATED);
        order3.setTotal(BigDecimal.valueOf(300.00));
        order3.setCreatedAt(LocalDateTime.now());

        orders = Arrays.asList(order1, order2, order3);
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação - primeira página")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithPaginationFirstPage() throws Exception {
        // Given
        when(listOrdersUseCase.execute(any())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação - última página")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithPaginationLastPage() throws Exception {
        // Given
        when(listOrdersUseCase.execute(any())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("Deve listar pedidos com paginação - página vazia")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithPaginationEmptyPage() throws Exception {
        // Given
        when(listOrdersUseCase.execute(any())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("page", "2")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("Deve listar pedidos com filtro por status e paginação")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithStatusFilterAndPagination() throws Exception {
        // Given
        List<OrderDto> confirmedOrders = orders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.CONFIRMED)
                .toList();
        when(listOrdersUseCase.execute(Order.OrderStatus.CONFIRMED)).thenReturn(confirmedOrders);

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("status", "CONFIRMED")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @DisplayName("Deve listar pedidos com lista vazia")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithEmptyList() throws Exception {
        // Given
        when(listOrdersUseCase.execute(any())).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }
}
