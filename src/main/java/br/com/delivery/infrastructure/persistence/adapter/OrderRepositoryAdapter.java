package br.com.delivery.infrastructure.persistence.adapter;

import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.port.OrderRepositoryPort;
import br.com.delivery.domain.valueobject.OrderItem;
import br.com.delivery.infrastructure.persistence.entity.OrderEntity;
import br.com.delivery.infrastructure.persistence.entity.OrderItemEntity;
import br.com.delivery.infrastructure.persistence.repository.OrderJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryAdapter.class);
    private final OrderJpaRepository jpaRepository;
    
    public OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(String id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        logger.debug("Buscando todos os pedidos");
        try {
            List<OrderEntity> entities = jpaRepository.findAllWithItems();
            logger.debug("Encontradas {} entidades de pedidos", entities.size());
            
            return entities.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os pedidos", e);
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
    @Transactional(readOnly = true)
    public List<Order> findByCustomerId(String customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(Order.OrderStatus status) {
        logger.debug("Buscando pedidos por status: {}", status);
        try {
            List<OrderEntity> entities = jpaRepository.findByStatus(status);
            logger.debug("Encontradas {} entidades de pedidos com status: {}", entities.size(), status);
            
            return entities.stream()
                    .map(this::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Erro ao buscar pedidos por status: {}", status, e);
            throw e;
        }
    }
    
    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotal()
        );
        
        // Converter itens do pedido
        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    entity,
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice()
            );
            entity.addItem(itemEntity);
        }
        
        return entity;
    }
    
    private Order toDomain(OrderEntity entity) {
        try {
            logger.debug("Convertendo OrderEntity para Order - ID: {}, Status: {}, Items: {}", 
                    entity.getId(), entity.getStatus(), entity.getItems().size());
            
            List<OrderItem> items = entity.getItems().stream()
                    .map(this::toDomainItem)
                    .collect(Collectors.toList());
            
            Order order = new Order(entity.getId(), entity.getCustomerId(), items);
            
            // Aplicar status atual
            if (entity.getStatus() != Order.OrderStatus.CREATED) {
                logger.debug("Aplicando status {} ao pedido {}", entity.getStatus(), entity.getId());
                order.advanceTo(entity.getStatus());
            }
            
            logger.debug("Conversão concluída - Order ID: {}, Status: {}", order.getId(), order.getStatus());
            return order;
        } catch (Exception e) {
            logger.error("Erro ao converter OrderEntity para Order - ID: {}, Status: {}, Items: {}", 
                    entity.getId(), entity.getStatus(), entity.getItems().size(), e);
            throw e;
        }
    }
    
    private OrderItem toDomainItem(OrderItemEntity entity) {
        return new OrderItem(
                entity.getProductId(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }
}
