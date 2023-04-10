package com.jmp.kafka.courier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.jmp.kafka.courier.dto.NotificationMessageDto;
import com.jmp.kafka.courier.dto.StatusDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaMessageProducer<NotificationMessageDto> producer;

    public Mono<StatusDto> startDelivery(final String orderId) {
        log.info("Order {} delivery has started", orderId);
        final var orderStatus = StatusDto.ON_DELIVERY;
        return producer.send(orderId, new NotificationMessageDto(orderStatus))
                .thenReturn(orderStatus);
    }

    public Mono<StatusDto> finishDelivery(final String orderId) {
        log.info("Order {} delivery has finished", orderId);
        final var orderStatus = StatusDto.DELIVERED;
        return producer.send(orderId, new NotificationMessageDto(orderStatus))
                .thenReturn(orderStatus);
    }

}
