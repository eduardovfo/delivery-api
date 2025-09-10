package br.com.delivery.infrastructure.persistence.repository;

import br.com.delivery.domain.entity.Order;
import br.com.delivery.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.customerId = :customerId")
    List<OrderEntity> findByCustomerId(@Param("customerId") String customerId);
    
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.status = :status")
    List<OrderEntity> findByStatus(@Param("status") Order.OrderStatus status);
    
    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items")
    List<OrderEntity> findAllWithItems();
}
