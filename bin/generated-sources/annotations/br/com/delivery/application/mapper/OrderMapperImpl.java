package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.OrderDto;
import br.com.delivery.application.dto.OrderItemDto;
import br.com.delivery.domain.entity.Order;
import br.com.delivery.domain.valueobject.OrderItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T01:16:21-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderDto orderDto = new OrderDto();

        orderDto.setTotal( order.getTotal() );
        orderDto.setId( order.getId() );
        orderDto.setCustomerId( order.getCustomerId() );
        orderDto.setItems( orderItemListToOrderItemDtoList( order.getItems() ) );
        orderDto.setStatus( order.getStatus() );
        orderDto.setCreatedAt( order.getCreatedAt() );

        return orderDto;
    }

    protected List<OrderItemDto> orderItemListToOrderItemDtoList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemDto> list1 = new ArrayList<OrderItemDto>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemMapper.toDto( orderItem ) );
        }

        return list1;
    }
}
