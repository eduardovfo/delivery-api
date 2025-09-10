package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CreateProductRequest;
import br.com.delivery.application.dto.ProductDto;
import br.com.delivery.application.mapper.ProductMapper;
import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.ProductRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final ProductMapper productMapper;
    
    public CreateProductUseCase(ProductRepositoryPort productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", allEntries = true),
        @CacheEvict(value = "product", allEntries = true)
    })
    public ProductDto execute(CreateProductRequest request) {
        // Gerar ID único para o produto
        String productId = UUID.randomUUID().toString();
        
        Product product = productMapper.toEntity(productId, request);
        
        // Salvar no repositório
        Product savedProduct = productRepository.save(product);
        
        // Converter para DTO de resposta
        return productMapper.toDto(savedProduct);
    }
}
