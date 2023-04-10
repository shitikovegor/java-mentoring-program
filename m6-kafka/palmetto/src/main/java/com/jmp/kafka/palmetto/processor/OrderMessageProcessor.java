package com.jmp.kafka.palmetto.processor;

import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.jmp.kafka.palmetto.dto.OrderMessageDto;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.palmetto.mapper.OrderMapper;
import com.jmp.kafka.palmetto.service.OrderService;
import com.jmp.kafka.processor.MessageProcessor;

@Component
@RequiredArgsConstructor
public class OrderMessageProcessor implements MessageProcessor<OrderMessageDto, StatusDto> {

    private final OrderService service;

    private final OrderMapper mapper;

    @Override
    public Mono<StatusDto> processMessage(final ConsumerRecord<String, OrderMessageDto> event) {
        final var orderId = new String(event.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value(),
                StandardCharsets.UTF_8);
        final var message = event.value();
        final var order = mapper.toOrderDto(message, orderId);
        return service.startCooking(order);
    }

}
