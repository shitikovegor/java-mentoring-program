package com.jmp.kafka.courier.processor;

import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.jmp.kafka.courier.dto.NotificationMessageDto;
import com.jmp.kafka.courier.dto.StatusDto;
import com.jmp.kafka.courier.service.OrderService;
import com.jmp.kafka.processor.MessageProcessor;

@Component
@RequiredArgsConstructor
public class NotificationMessageProcessor implements MessageProcessor<NotificationMessageDto, StatusDto> {

    private final OrderService service;

    @Override
    public Mono<StatusDto> processMessage(final ConsumerRecord<String, NotificationMessageDto> event) {
        final var orderId = new String(event.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value(),
                StandardCharsets.UTF_8);
        final var message = event.value();
        return message.getStatus() == StatusDto.READY ? service.startDelivery(orderId) : Mono.empty();
    }

}
