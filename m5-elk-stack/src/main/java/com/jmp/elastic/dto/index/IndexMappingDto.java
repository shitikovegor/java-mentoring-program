package com.jmp.elastic.dto.index;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexMappingDto {

    private Map<String, FieldTypeDto> properties;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldTypeDto {

        private String type;

    }

}
