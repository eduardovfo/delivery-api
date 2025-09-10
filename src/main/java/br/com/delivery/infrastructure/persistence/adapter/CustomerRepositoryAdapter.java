package br.com.delivery.infrastructure.persistence.adapter;

import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import br.com.delivery.infrastructure.persistence.entity.CustomerEntity;
import br.com.delivery.infrastructure.persistence.repository.CustomerJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
    
    private final CustomerJpaRepository jpaRepository;
    
    public CustomerRepositoryAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    @Transactional
    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        CustomerEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    public Optional<Customer> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }
    
    @Override
    public List<Customer> findAll() {
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
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(this::toDomain);
    }
    
    @Override
    public Optional<Customer> findByDocument(String document) {
        return jpaRepository.findByDocument(document)
                .map(this::toDomain);
    }
    
    private CustomerEntity toEntity(Customer customer) {
        return new CustomerEntity(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getDocument()
        );
    }
    
    private Customer toDomain(CustomerEntity entity) {
        return new Customer(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getDocument()
        );
    }
}
