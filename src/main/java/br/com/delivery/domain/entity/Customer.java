package br.com.delivery.domain.entity;

import java.util.Objects;

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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getDocument() {
        return document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", document='" + document + '\'' +
                '}';
    }
}
