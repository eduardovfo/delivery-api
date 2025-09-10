package br.com.delivery.infrastructure.web.controller;

import br.com.delivery.application.dto.CreateOrderRequest;
import br.com.delivery.application.dto.CreateOrderItemRequest;
import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.usecase.CreateOrderUseCase;
import br.com.delivery.application.usecase.GetOrderUseCase;
import br.com.delivery.application.usecase.ListOrdersUseCase;
import br.com.delivery.application.usecase.UpdateOrderStatusUseCase;
import br.com.delivery.domain.entity.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("Testes do OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private ListOrdersUseCase listOrdersUseCase;

    @MockBean
    private UpdateOrderStatusUseCase updateOrderStatusUseCase;

    private CreateOrderRequest createOrderRequest;
    private OrderDto orderDto;

    @BeforeEach
    void setUp() {
        CreateOrderItemRequest itemRequest = new CreateOrderItemRequest();
        itemRequest.setProductId("product-123");
        itemRequest.setQuantity(2);

        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId("customer-123");
        createOrderRequest.setItems(Arrays.asList(itemRequest));

        orderDto = new OrderDto();
        orderDto.setId("order-123");
        orderDto.setCustomerId("customer-123");
        orderDto.setStatus(Order.OrderStatus.CREATED);
        orderDto.setCreatedAt(LocalDateTime.now());
        orderDto.setTotal(new BigDecimal("59.98"));
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    @WithMockUser(authorities = "SCOPE_orders:write")
    void shouldCreateOrderSuccessfully() throws Exception {
        // Given
        when(createOrderUseCase.execute(any(CreateOrderRequest.class))).thenReturn(orderDto);

        // When & Then
        mockMvc.perform(post("/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.total").value(59.98));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando dados são inválidos")
    @WithMockUser(authorities = "SCOPE_orders:write")
    void shouldReturn400WhenDataIsInvalid() throws Exception {
        // Given
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        // customerId e items são obrigatórios

        // When & Then
        mockMvc.perform(post("/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldGetOrderByIdSuccessfully() throws Exception {
        // Given
        when(getOrderUseCase.execute("order-123")).thenReturn(java.util.Optional.of(orderDto));

        // When & Then
        mockMvc.perform(get("/v1/orders/order-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.customerId").value("customer-123"))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("Deve listar pedidos com sucesso")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersSuccessfully() throws Exception {
        // Given
        List<OrderDto> orders = Arrays.asList(orderDto);
        when(listOrdersUseCase.execute(any())).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("order-123"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    @DisplayName("Deve listar pedidos com filtro por status")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldListOrdersWithStatusFilter() throws Exception {
        // Given
        List<OrderDto> orders = Arrays.asList(orderDto);
        when(listOrdersUseCase.execute(Order.OrderStatus.CREATED)).thenReturn(orders);

        // When & Then
        mockMvc.perform(get("/v1/orders")
                .param("status", "CREATED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].status").value("CREATED"));
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    @WithMockUser(authorities = "SCOPE_orders:write")
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        // Given
        OrderDto updatedOrder = new OrderDto();
        updatedOrder.setId("order-123");
        updatedOrder.setStatus(Order.OrderStatus.CONFIRMED);
        when(updateOrderStatusUseCase.execute(anyString(), any())).thenReturn(java.util.Optional.of(updatedOrder));

        // When & Then
        mockMvc.perform(patch("/v1/orders/order-123/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("order-123"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("Deve retornar erro 403 quando usuário não tem permissão")
    @WithMockUser(authorities = "SCOPE_orders:read")
    void shouldReturn403WhenUserLacksPermission() throws Exception {
        // When & Then
        mockMvc.perform(post("/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar erro 401 quando usuário não está autenticado")
    void shouldReturn401WhenUserNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/v1/orders"))
                .andExpect(status().isUnauthorized());
    }
}
