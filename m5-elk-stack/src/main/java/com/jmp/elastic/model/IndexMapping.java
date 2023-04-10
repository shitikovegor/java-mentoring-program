package com.jmp.elastic.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexMapping {

    @NotNull
    private Map<String, FieldType> properties;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldType {

        @NotNull
        private String type;

    }

}
