package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.domain.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    @Mapping(target = "id", ignore = true)
    Customer toEntity(CreateCustomerRequest request);
    
    CustomerDto toDto(Customer customer);
    
    default Customer toEntity(String id, CreateCustomerRequest request) {
        return new Customer(id, request.getName(), request.getEmail(), request.getDocument());
    }
}
