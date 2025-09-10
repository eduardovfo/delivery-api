package br.com.delivery.infrastructure.web.controller;

import br.com.delivery.application.dto.CreateProductRequest;
import br.com.delivery.application.dto.ProductDto;
import br.com.delivery.application.usecase.CreateProductUseCase;
import br.com.delivery.application.usecase.GetProductUseCase;
import br.com.delivery.application.usecase.ListProductsUseCase;
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
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "API para gerenciamento de produtos")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final ListProductsUseCase listProductsUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                            GetProductUseCase getProductUseCase,
                            ListProductsUseCase listProductsUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_products:write')")
    @Operation(summary = "Criar produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "name": "Notebook Dell",
                                        "price": 2999.99
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDto product = createProductUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_products:read')")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "id": "123e4567-e89b-12d3-a456-426614174000",
                                        "name": "Notebook Dell",
                                        "price": 2999.99
                                    }
                                    """))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(mediaType = "application/problem+json"))
    })
    public ResponseEntity<ProductDto> getProduct(
            @Parameter(description = "ID do produto", required = true)
            @PathVariable String id) {
        
        return getProductUseCase.execute(id)
                .map(product -> ResponseEntity.ok(product))
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_products:read')")
    @Operation(summary = "Listar produtos", description = "Retorna uma lista paginada de produtos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "content": [
                                            {
                                                "id": "123e4567-e89b-12d3-a456-426614174000",
                                                "name": "Notebook Dell",
                                                "price": 2999.99
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
    public ResponseEntity<PageResponse<ProductDto>> listProducts(
            @Parameter(description = "Número da página (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "20") int size) {
        
        List<ProductDto> products = listProductsUseCase.execute();
        
        int start = page * size;
        int end = Math.min(start + size, products.size());
        List<ProductDto> pageContent = products.subList(start, end);
        
        PageResponse<ProductDto> response = new PageResponse<>(
                pageContent,
                page,
                size,
                products.size(),
                (int) Math.ceil((double) products.size() / size),
                page == 0,
                page >= (products.size() - 1) / size
        );
        
        return ResponseEntity.ok(response);
    }
}
