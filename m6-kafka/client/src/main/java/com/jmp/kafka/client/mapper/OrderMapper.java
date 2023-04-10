package com.jmp.kafka.client.mapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.jmp.kafka.client.dto.OrderDto;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.client.dto.OrderRequestDto;
import com.jmp.kafka.client.dto.ProductOrderDto;
import com.jmp.kafka.client.model.Order;
import com.jmp.kafka.client.model.ProductOrder;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    Order toOrder(final OrderRequestDto requestDto);

    OrderDto toOrderDto(final Order order);

    ProductOrder toProductOrder(final ProductOrderDto orderDto);

    List<ProductOrder> toProductOrders(final List<ProductOrderDto> orderDtos);

    OrderMessageDto toOrderMessageDto(final OrderDto order);

    @AfterMapping
    default void fillOrderDetails(@MappingTarget final Order.OrderBuilder order) {
        order.id(UUID.randomUUID().toString());
        order.creationTime(Instant.now());
    }

}
