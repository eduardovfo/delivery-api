package br.com.delivery.domain.port;

import br.com.delivery.domain.entity.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(Order.OrderStatus status);
}
