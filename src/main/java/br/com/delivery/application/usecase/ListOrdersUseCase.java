package br.com.delivery.application.usecase;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.mapper.OrderMapper;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.port.OrderRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListOrdersUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(ListOrdersUseCase.class);
    private final OrderRepositoryPort orderRepository;
    private final OrderMapper orderMapper;
    
    public ListOrdersUseCase(OrderRepositoryPort orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }
    
    @Cacheable(value = "orders", key = "#status != null ? #status.name() : 'ALL'")
    public List<OrderDto> execute(Order.OrderStatus status) {
        try {
            logger.debug("Executando ListOrdersUseCase - status: {}", status);
            
            List<Order> orders;
            
            if (status != null) {
                logger.debug("Buscando pedidos por status: {}", status);
                orders = orderRepository.findByStatus(status);
            } else {
                logger.debug("Buscando todos os pedidos");
                orders = orderRepository.findAll();
            }
            
            logger.debug("Encontrados {} pedidos no domínio", orders.size());
            
            List<OrderDto> dtos = orders.stream()
                    .map(orderMapper::toDto)
                    .collect(Collectors.toList());
            
            logger.debug("Convertidos {} pedidos para DTOs", dtos.size());
            return dtos;
            
        } catch (Exception e) {
            logger.error("Erro no ListOrdersUseCase - status: {}", status, e);
            throw e;
        }
    }
    
    @Cacheable(value = "orders", key = "'ALL'")
    public List<OrderDto> execute() {
        return execute(null);
    }
}
