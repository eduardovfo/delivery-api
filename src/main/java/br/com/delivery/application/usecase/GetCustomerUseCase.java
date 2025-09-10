package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.mapper.CustomerMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    private final CustomerMapper customerMapper;
    
    public GetCustomerUseCase(CustomerRepositoryPort customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    @Cacheable(value = "customer", key = "#customerId")
    public Optional<CustomerDto> execute(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do cliente é obrigatório");
        }
        
        Optional<Customer> customer = customerRepository.findById(customerId.trim());
        return customer.map(customerMapper::toDto);
    }
}
