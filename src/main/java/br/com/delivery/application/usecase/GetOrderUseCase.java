package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.mapper.OrderMapper;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.port.OrderRepositoryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetOrderUseCase {
    
    private final OrderRepositoryPort orderRepository;
    private final OrderMapper orderMapper;
    
    public GetOrderUseCase(OrderRepositoryPort orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    
    @Cacheable(value = "order", key = "#orderId")
    public Optional<OrderDto> execute(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do pedido é obrigatório");
        }
        
        Optional<Order> order = orderRepository.findById(orderId.trim());
        return order.map(orderMapper::toDto);
    }
}
