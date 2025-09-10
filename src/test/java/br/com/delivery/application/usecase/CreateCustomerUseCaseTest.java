package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CreateCustomerRequest;
import br.com.delivery.application.dto.CustomerDto;
import br.com.delivery.application.mapper.CustomerMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do CreateCustomerUseCase")
class CreateCustomerUseCaseTest {

    @Mock
    private CustomerRepositoryPort customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CreateCustomerUseCase createCustomerUseCase;

    private CreateCustomerRequest request;
    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        request = new CreateCustomerRequest();
        request.setName("João Silva");
        request.setEmail("joao@email.com");
        request.setDocument("12345678901");

        customer = new Customer("customer-123", "João Silva", "joao@email.com", "12345678901");
        
        customerDto = new CustomerDto();
        customerDto.setId("customer-123");
        customerDto.setName("João Silva");
        customerDto.setEmail("joao@email.com");
        customerDto.setDocument("12345678901");
    }

    @Test
    @DisplayName("Deve criar cliente com sucesso")
    void shouldCreateCustomerSuccessfully() {
        // Given
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(customer)).thenReturn(customerDto);

        // When
        CustomerDto result = createCustomerUseCase.execute(request);

        // Then
        assertNotNull(result);
        assertEquals("customer-123", result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        assertEquals("12345678901", result.getDocument());

        verify(customerRepository).save(any(Customer.class));
        verify(customerMapper).toDto(customer);
    }

    @Test
    @DisplayName("Deve gerar ID único para o cliente")
    void shouldGenerateUniqueIdForCustomer() {
        // Given
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customer; // Retorna o mesmo cliente para verificar se foi salvo
        });
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        // When
        createCustomerUseCase.execute(request);

        // Then
        verify(customerRepository).save(argThat(customer -> 
            customer.getId() != null && !customer.getId().isEmpty()
        ));
    }

    @Test
    @DisplayName("Deve mapear dados corretamente do request para entidade")
    void shouldMapDataCorrectlyFromRequestToEntity() {
        // Given
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customer;
        });
        when(customerMapper.toDto(any(Customer.class))).thenReturn(customerDto);

        // When
        createCustomerUseCase.execute(request);

        // Then
        verify(customerRepository).save(argThat(customer -> 
            "João Silva".equals(customer.getName()) &&
            "joao@email.com".equals(customer.getEmail()) &&
            "12345678901".equals(customer.getDocument())
        ));
    }
}
