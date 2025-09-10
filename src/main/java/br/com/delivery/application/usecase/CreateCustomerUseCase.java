package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.mapper.CustomerMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateCustomerUseCase {
    
    private final CustomerRepositoryPort customerRepository;
    private final CustomerMapper customerMapper;
    
    public CreateCustomerUseCase(CustomerRepositoryPort customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "customers", allEntries = true),
        @CacheEvict(value = "customer", allEntries = true)
    })
    public CustomerDto execute(CreateCustomerRequest request) {
        // Verificar se já existe cliente com o mesmo email
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este email");
        }
        
        // Verificar se já existe cliente com o mesmo documento
        if (customerRepository.findByDocument(request.getDocument()).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este documento");
        }
        
        // Gerar ID único para o cliente
        String customerId = UUID.randomUUID().toString();
        
        Customer customer = customerMapper.toEntity(customerId, request);
        
        // Salvar no repositório
        Customer savedCustomer = customerRepository.save(customer);
        
        // Converter para DTO de resposta
        return customerMapper.toDto(savedCustomer);
    }
}
