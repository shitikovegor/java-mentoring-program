package com.jmp.kafka.palmetto.mapper;

import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.jmp.kafka.palmetto.dto.ProductAddRequestDto;
import com.jmp.kafka.palmetto.dto.ProductDto;
import com.jmp.kafka.palmetto.model.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(final ProductAddRequestDto requestDto);

    ProductDto toProductDto(final Product product);

    @AfterMapping
    default void fillProductId(@MappingTarget final Product.ProductBuilder product) {
        product.id(UUID.randomUUID().toString());
    }

}
