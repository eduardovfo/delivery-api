package br.com.delivery.infrastructure.persistence.adapter;

import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.ProductRepositoryPort;
import br.com.delivery.infrastructure.persistence.entity.ProductEntity;
import br.com.delivery.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    
    private final ProductJpaRepository jpaRepository;
    
    public ProductRepositoryAdapter(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    @Transactional
    public Product save(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Product> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }
    
    @Override
    public List<Product> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public List<Product> findByNameContaining(String name) {
        return jpaRepository.findByNameContaining(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
    
    private Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getPrice()
        );
    }
}
