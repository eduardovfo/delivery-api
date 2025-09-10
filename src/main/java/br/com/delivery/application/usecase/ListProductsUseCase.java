package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.ProductDto;
import br.com.delivery.application.mapper.ProductMapper;
import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.ProductRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListProductsUseCase {
    
    private final ProductRepositoryPort productRepository;
    private final ProductMapper productMapper;
    
    public ListProductsUseCase(ProductRepositoryPort productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }
    
    @Cacheable(value = "products")
    public List<ProductDto> execute() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}
