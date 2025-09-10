package br.com.delivery.application.mapper;

import br.com.delivery.application.dto.OrderItemDto;
import br.com.delivery.domain.valueobject.OrderItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-10T01:16:21-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.43.0.v20250819-1513, environment: Java 21.0.8 (Eclipse Adoptium)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItemDto toDto(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemDto orderItemDto = new OrderItemDto();

        orderItemDto.setProductId( orderItem.getProductId() );
        orderItemDto.setQuantity( orderItem.getQuantity() );
        orderItemDto.setUnitPrice( orderItem.getUnitPrice() );

        return orderItemDto;
    }
}
