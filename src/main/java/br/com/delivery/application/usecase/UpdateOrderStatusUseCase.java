package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.dto.UpdateOrderStatusRequest;
import br.com.delivery.application.mapper.OrderMapper;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.port.OrderRepositoryPort;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UpdateOrderStatusUseCase {
    
    private final OrderRepositoryPort orderRepository;
    private final OrderMapper orderMapper;
    
    public UpdateOrderStatusUseCase(OrderRepositoryPort orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "orders", allEntries = true),
        @CacheEvict(value = "order", key = "#orderId")
    })
    public Optional<OrderDto> execute(String orderId, UpdateOrderStatusRequest request) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do pedido é obrigatório");
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId.trim());
        
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Order order = orderOpt.get();
        
        // Atualizar status do pedido
        order.advanceTo(request.getStatus());
        
        // Salvar alterações
        Order savedOrder = orderRepository.save(order);
        
        // Converter para DTO de resposta
        return Optional.of(orderMapper.toDto(savedOrder));
    }
}
