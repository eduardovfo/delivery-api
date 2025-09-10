package br.com.delivery.infrastructure.web.controller;

import br.com.delivery.application.dto.CreateOrderRequest;
import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.dto.UpdateOrderStatusRequest;
import br.com.delivery.application.usecase.CreateOrderUseCase;
import br.com.delivery.application.usecase.GetOrderUseCase;
import br.com.delivery.application.usecase.ListOrdersUseCase;
import br.com.delivery.application.usecase.UpdateOrderStatusUseCase;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.infrastructure.web.dto.PageResponse;
import br.com.delivery.infrastructure.web.exception.ResourceNotFoundException;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "API para gerenciamento de pedidos")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                          GetOrderUseCase getOrderUseCase,
                          ListOrdersUseCase listOrdersUseCase,
                          UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_orders:write')")
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class),
                            examples = @ExampleObject(value = """
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
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou cliente/produto não encontrado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderDto order = createOrderUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_orders:read')")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class),
                            examples = @ExampleObject(value = """
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
                                    """))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<OrderDto> getOrder(
            @Parameter(description = "ID do pedido", required = true)
            @PathVariable String id) {
        
        return getOrderUseCase.execute(id)
                .map(order -> ResponseEntity.ok(order))
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_orders:read')")
    @Operation(summary = "Listar pedidos", description = "Retorna uma lista paginada de pedidos com filtro opcional por status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "content": [
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
                                        ],
                                        "page": 0,
                                        "size": 20,
                                        "totalElements": 1,
                                        "totalPages": 1,
                                        "first": true,
                                        "last": true
                                    }
                                    """)))
    })
    public ResponseEntity<PageResponse<OrderDto>> listOrders(
            @Parameter(description = "Filtro por status do pedido")
            @RequestParam(required = false) Order.OrderStatus status,
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            logger.info("Iniciando listagem de pedidos - status: {}, page: {}, size: {}", status, page, size);
            
            List<OrderDto> orders = listOrdersUseCase.execute(status);
            logger.debug("Pedidos encontrados: {} itens", orders.size());
        
            // Simular paginação simples (em produção, implementar paginação real)
            int start = page * size;
            int end = Math.min(start + size, orders.size());
            
            // Validar índices para evitar IndexOutOfBoundsException
            if (start >= orders.size()) {
                start = orders.size();
                end = orders.size();
            }
            
            List<OrderDto> pageContent = start < orders.size() ? 
                orders.subList(start, end) : 
                new ArrayList<>();
            
            int totalPages = orders.size() == 0 ? 0 : (int) Math.ceil((double) orders.size() / size);
            boolean isLast = page >= totalPages - 1 || totalPages == 0;
            
            logger.debug("Paginação - start: {}, end: {}, totalPages: {}, isLast: {}", start, end, totalPages, isLast);
            
            PageResponse<OrderDto> response = new PageResponse<>(
                    pageContent,
                    page,
                    size,
                    orders.size(),
                    totalPages,
                    page == 0,
                    isLast
            );
            
            logger.info("Listagem de pedidos concluída com sucesso - {} itens retornados", pageContent.size());
            return ResponseEntity.ok(response);
        
        } catch (Exception e) {
            logger.error("Erro na listagem de pedidos - status: {}, page: {}, size: {}", status, page, size, e);
            throw e;
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('SCOPE_orders:write')")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class),
                            examples = @ExampleObject(value = """
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
                                        "status": "CONFIRMED",
                                        "createdAt": "2025-01-27T10:30:00Z",
                                        "total": 59.98
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<OrderDto> updateOrderStatus(
            @Parameter(description = "ID do pedido", required = true)
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        
        return updateOrderStatusUseCase.execute(id, request)
                .map(order -> ResponseEntity.ok(order))
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + id));
    }
}
