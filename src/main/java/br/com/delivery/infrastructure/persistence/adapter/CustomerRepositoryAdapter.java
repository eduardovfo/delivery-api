package br.com.delivery.infrastructure.persistence.adapter;

import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import br.com.delivery.infrastructure.persistence.entity.CustomerEntity;
import br.com.delivery.infrastructure.persistence.repository.CustomerJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryAdapter.class);
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
        try {
            List<CustomerEntity> entities = jpaRepository.findAll();
            logger.info("Encontradas {} entidades CustomerEntity no banco", entities.size());
            
            for (CustomerEntity entity : entities) {
                logger.info("CustomerEntity: id={}, name={}, email={}, document={}", 
                    entity.getId(), entity.getName(), entity.getEmail(), entity.getDocument());
            }
            
            List<Customer> customers = entities.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
            
            logger.info("Convertidas {} entidades para Customer domain", customers.size());
            for (Customer customer : customers) {
                logger.info("Customer domain: id={}, name={}, email={}, document={}", 
                    customer.getId(), customer.getName(), customer.getEmail(), customer.getDocument());
            }
            
            return customers;
        } catch (Exception e) {
            logger.error("Erro ao buscar clientes", e);
            throw e;
        }
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
