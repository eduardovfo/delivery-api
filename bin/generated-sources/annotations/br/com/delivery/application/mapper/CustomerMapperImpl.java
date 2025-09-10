package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.domain.entity.Customer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T01:16:21-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CreateCustomerRequest request) {
        if ( request == null ) {
            return null;
        }

        String name = null;
        String email = null;
        String document = null;

        name = request.getName();
        email = request.getEmail();
        document = request.getDocument();

        String id = null;

        Customer customer = new Customer( id, name, email, document );

        return customer;
    }

    @Override
    public CustomerDto toDto(Customer customer) {
        if ( customer == null ) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();

        customerDto.setId( customer.getId() );
        customerDto.setName( customer.getName() );
        customerDto.setEmail( customer.getEmail() );
        customerDto.setDocument( customer.getDocument() );

        return customerDto;
    }
}
