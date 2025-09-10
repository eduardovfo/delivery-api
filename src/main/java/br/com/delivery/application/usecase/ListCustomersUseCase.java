package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.mapper.CustomerMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListCustomersUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ListCustomersUseCase.class);
    private final CustomerRepositoryPort customerRepository;
    private final CustomerMapper customerMapper;
    
    public ListCustomersUseCase(CustomerRepositoryPort customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }
    
    @Cacheable(value = "customers")
    public List<CustomerDto> execute() {
        try {
            logger.info("Iniciando busca de clientes");
            List<Customer> customers = customerRepository.findAll();
            logger.info("Encontrados {} clientes no repositório", customers.size());
            
            List<CustomerDto> customerDtos = customers.stream()
                    .map(customer -> {
                        CustomerDto dto = customerMapper.toDto(customer);
                        logger.info("Mapeando Customer para DTO: id={}, name={}, email={}, document={}", 
                            customer.getId(), customer.getName(), customer.getEmail(), customer.getDocument());
                        logger.info("DTO resultante: id={}, name={}, email={}, document={}", 
                            dto.getId(), dto.getName(), dto.getEmail(), dto.getDocument());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            logger.info("Retornando {} CustomerDtos", customerDtos.size());
            return customerDtos;
        } catch (Exception e) {
            logger.error("Erro ao listar clientes", e);
            throw e;
        }
    }
}
