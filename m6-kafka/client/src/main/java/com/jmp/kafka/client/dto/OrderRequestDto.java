package com.jmp.kafka.client.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    @NotEmpty
    private String name;

    @NotEmpty
    private String phone;

    @NotEmpty
    private List<ProductOrderDto> basket;

}
