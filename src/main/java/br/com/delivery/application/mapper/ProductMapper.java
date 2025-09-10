package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.CreateProductRequest;
import br.com.delivery.application.dto.ProductDto;
import br.com.delivery.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    @Mapping(target = "id", ignore = true)
    Product toEntity(CreateProductRequest request);
    
    ProductDto toDto(Product product);
    
    default Product toEntity(String id, CreateProductRequest request) {
        return new Product(id, request.getName(), request.getPrice());
    }
}
