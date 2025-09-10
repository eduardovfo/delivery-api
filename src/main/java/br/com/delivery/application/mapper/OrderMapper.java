package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {
    
    OrderDto toDto(Order order);
}
