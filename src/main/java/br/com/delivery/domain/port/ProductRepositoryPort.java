package br.com.delivery.domain.port;

import br.com.delivery.domain.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    List<Product> findByNameContaining(String name);
}
