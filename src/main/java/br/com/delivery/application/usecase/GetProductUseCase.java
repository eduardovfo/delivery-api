package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.ProductDto;
import br.com.delivery.application.mapper.ProductMapper;
import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.ProductRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetProductUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final ProductMapper productMapper;
    
    public GetProductUseCase(ProductRepositoryPort productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    @Cacheable(value = "product", key = "#productId")
    public Optional<ProductDto> execute(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do produto é obrigatório");
        }
        
        Optional<Product> product = productRepository.findById(productId.trim());
        return product.map(productMapper::toDto);
    }
}
