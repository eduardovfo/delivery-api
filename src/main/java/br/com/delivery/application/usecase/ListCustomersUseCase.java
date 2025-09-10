package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.mapper.CustomerMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListCustomersUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    private final CustomerMapper customerMapper;
    
    public ListCustomersUseCase(CustomerRepositoryPort customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    @Cacheable(value = "customers")
    public List<CustomerDto> execute() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }
}
