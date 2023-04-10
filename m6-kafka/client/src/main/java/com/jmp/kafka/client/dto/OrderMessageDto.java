package com.jmp.kafka.client.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessageDto {

    private Instant creationTime;

    private List<ProductOrderDto> basket;

}
