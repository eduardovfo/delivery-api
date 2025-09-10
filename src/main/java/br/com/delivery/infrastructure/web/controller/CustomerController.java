package br.com.delivery.infrastructure.web.controller;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.usecase.CreateCustomerUseCase;
import br.com.delivery.application.usecase.GetCustomerUseCase;
import br.com.delivery.application.usecase.ListCustomersUseCase;
import br.com.delivery.infrastructure.web.dto.PageResponse;
import br.com.delivery.infrastructure.web.exception.ResourceNotFoundException;
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
@RequestMapping("/v1/customers")
@Tag(name = "Customers", description = "API para gerenciamento de clientes")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final ListCustomersUseCase listCustomersUseCase;

    public CustomerController(CreateCustomerUseCase createCustomerUseCase,
                             GetCustomerUseCase getCustomerUseCase,
                             ListCustomersUseCase listCustomersUseCase) {
        this.createCustomerUseCase = createCustomerUseCase;
        this.getCustomerUseCase = getCustomerUseCase;
        this.listCustomersUseCase = listCustomersUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_customers:write')")
    @Operation(summary = "Criar cliente", description = "Cria um novo cliente no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "name": "João Silva",
                                        "email": "joao@email.com",
                                        "document": "12345678901"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "409", description = "Email ou documento já existem",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerDto customer = createCustomerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_customers:read')")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomerDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "name": "João Silva",
                                        "email": "joao@email.com",
                                        "document": "12345678901"
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<CustomerDto> getCustomer(
            @Parameter(description = "ID do cliente", required = true)
            @PathVariable String id) {
        
        return getCustomerUseCase.execute(id)
                .map(customer -> ResponseEntity.ok(customer))
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com ID: " + id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_customers:read')")
    @Operation(summary = "Listar clientes", description = "Retorna uma lista paginada de clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(value = """
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
                                    """)))
    })
    public ResponseEntity<PageResponse<CustomerDto>> listCustomers(
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size) {
        
        List<CustomerDto> customers = listCustomersUseCase.execute();
        
        int start = page * size;
        int end = Math.min(start + size, customers.size());
        List<CustomerDto> pageContent = customers.subList(start, end);
        
        PageResponse<CustomerDto> response = new PageResponse<>(
                pageContent,
                page,
                size,
                customers.size(),
                (int) Math.ceil((double) customers.size() / size),
                page == 0,
                page >= (customers.size() - 1) / size
        );
        
        return ResponseEntity.ok(response);
    }
}
