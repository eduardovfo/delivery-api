package br.com.delivery.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Schema(description = "Representa um produto no sistema")
@Data
@EqualsAndHashCode(of = "id")
public class ProductDto {
    @Schema(description = "Identificador único do produto", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Nome do produto", example = "Notebook Dell Inspiron 15")
    private String name;

    @Schema(description = "Preço do produto", example = "2999.99")
    private BigDecimal price;

    public ProductDto() {}

}
