package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.CreateOrderRequest;
import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.mapper.OrderMapper;
import br.com.delivery.domain.entity.Customer;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.entity.Product;
import br.com.delivery.domain.port.CustomerRepositoryPort;
import br.com.delivery.domain.port.OrderRepositoryPort;
import br.com.delivery.domain.port.ProductRepositoryPort;
import br.com.delivery.domain.valueobject.OrderItem;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CreateOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    private final CustomerRepositoryPort customerRepository;
    private final ProductRepositoryPort productRepository;
    private final OrderMapper orderMapper;
    
    public CreateOrderUseCase(OrderRepositoryPort orderRepository, 
                             CustomerRepositoryPort customerRepository,
                             ProductRepositoryPort productRepository,
                             OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "order", allEntries = true)
    })
    public OrderDto execute(CreateOrderRequest request) {
        // Validar se o cliente existe
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Validar produtos e montar itens do pedido com preços atuais
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + itemRequest.getProductId()));
            
            // Usar o preço atual do produto
            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    itemRequest.getQuantity(),
                    product.getPrice()
            );
            
            orderItems.add(orderItem);
        }
        
        // Gerar ID único para o pedido
        String orderId = UUID.randomUUID().toString();
        
        Order order = new Order(orderId, customer.getId(), orderItems);
        
        // Salvar no repositório
        Order savedOrder = orderRepository.save(order);
        
        // Converter para DTO de resposta
        return orderMapper.toDto(savedOrder);
    }
}
