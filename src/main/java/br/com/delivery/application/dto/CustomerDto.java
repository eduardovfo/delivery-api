package br.com.delivery.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Representa um cliente no sistema")
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

    public CustomerDto(String id, String name, String email, String document) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.document = document;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerDto that = (CustomerDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CustomerDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", document='" + document + '\'' +
                '}';
    }
}
