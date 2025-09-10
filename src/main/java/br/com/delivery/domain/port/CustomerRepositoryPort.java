package br.com.delivery.domain.port;

import br.com.delivery.domain.entity.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    Customer save(Customer customer);
    Optional<Customer> findById(String id);
    List<Customer> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByDocument(String document);
}
