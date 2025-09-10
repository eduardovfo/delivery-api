package br.com.delivery.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final String document;

    public Customer(String id, String name, String email, String document) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new IllegalArgumentException("Customer email must be valid");
        }
        if (document == null || document.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer document cannot be null or empty");
        }
        
        this.id = id.trim();
        this.name = name.trim();
        this.email = email.trim();
        this.document = document.trim();
    }

}
