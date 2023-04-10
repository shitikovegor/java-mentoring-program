package com.jmp.kafka.palmetto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.jmp.kafka.palmetto.dto.OrderDto;
import com.jmp.kafka.palmetto.dto.OrderMessageDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "id", source = "orderId")
    OrderDto toOrderDto(final OrderMessageDto message, final String orderId);

}
