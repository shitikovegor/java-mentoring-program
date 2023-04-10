package com.jmp.kafka.palmetto.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductDto {

    private String id;

    private String name;

}
