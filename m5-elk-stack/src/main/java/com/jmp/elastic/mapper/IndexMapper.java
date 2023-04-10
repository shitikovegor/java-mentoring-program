package com.jmp.elastic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.jmp.elastic.dto.index.IndexMappingDto;
import com.jmp.elastic.model.IndexMapping;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IndexMapper {

    IndexMappingDto toMappingDto(IndexMapping mapping);

    IndexMappingDto.FieldTypeDto toFieldTypeDto(IndexMapping.FieldType fieldType);

}
