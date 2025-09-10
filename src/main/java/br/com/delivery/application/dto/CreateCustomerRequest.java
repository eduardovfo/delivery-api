package br.com.delivery.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criação de um novo cliente")
public class CreateCustomerRequest {
    @Schema(description = "Nome completo do cliente", example = "João Silva", required = true)
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @Schema(description = "Email do cliente", example = "joao@email.com", required = true)
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String email;

    @Schema(description = "Documento do cliente (CPF/CNPJ)", example = "12345678901", required = true)
    @NotBlank(message = "Documento é obrigatório")
    @Size(min = 11, max = 14, message = "Documento deve ter entre 11 e 14 caracteres")
    private String document;

    public CreateCustomerRequest() {}

    public CreateCustomerRequest(String name, String email, String document) {
        this.name = name;
        this.email = email;
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
