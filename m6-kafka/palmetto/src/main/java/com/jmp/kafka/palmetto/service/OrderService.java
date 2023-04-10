package com.jmp.kafka.palmetto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.jmp.kafka.palmetto.dto.NotificationMessageDto;
import com.jmp.kafka.palmetto.dto.OrderDto;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaMessageProducer<NotificationMessageDto> producer;

    public Mono<StatusDto> startCooking(final OrderDto order) {
        log.info("Start cooking order {}", order.getId());
        final var orderStatus = StatusDto.COOKING;
        return producer.send(order.getId(), new NotificationMessageDto(orderStatus))
                .thenReturn(orderStatus);
    }

    public Mono<StatusDto> finishCooking(final String orderId) {
        log.info("Finish cooking order {}", orderId);
        final var orderStatus = StatusDto.READY;
        return producer.send(orderId, new NotificationMessageDto(orderStatus))
                .thenReturn(orderStatus);
    }

}
