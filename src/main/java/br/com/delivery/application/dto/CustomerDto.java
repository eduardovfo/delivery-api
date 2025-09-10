package br.com.delivery.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "Representa um cliente no sistema")
@Data
@EqualsAndHashCode(of = "id")
public class CustomerDto {
    @Schema(description = "Identificador único do cliente", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;

    @Schema(description = "Nome completo do cliente", example = "João Silva")
    private String name;

    @Schema(description = "Email do cliente", example = "joao@email.com")
    private String email;

    @Schema(description = "Documento do cliente (CPF/CNPJ)", example = "12345678901")
    private String document;

    public CustomerDto() {}

}
