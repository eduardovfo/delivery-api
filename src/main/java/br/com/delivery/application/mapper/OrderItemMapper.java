package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.OrderItemDto;
import br.com.delivery.domain.valueobject.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    OrderItemDto toDto(OrderItem orderItem);
    
    default OrderItem toEntity(String productId, int quantity, java.math.BigDecimal unitPrice) {
        return new OrderItem(productId, quantity, unitPrice);
    }
}
