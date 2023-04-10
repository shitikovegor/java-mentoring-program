package com.jmp.kafka.client.processor;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.client.mapper.NotificationMapper;
import com.jmp.kafka.client.service.NotificationService;
import com.jmp.kafka.processor.MessageProcessor;

@Component
@RequiredArgsConstructor
public class NotificationMessageProcessor implements MessageProcessor<NotificationMessageDto, NotificationDto> {

    private final NotificationService service;

    private final NotificationMapper mapper;

    @Override
    public Mono<NotificationDto> processMessage(final ConsumerRecord<String, NotificationMessageDto> event) {
        final var orderId = new String(event.headers().lastHeader(KafkaHeaders.CORRELATION_ID).value(),
                StandardCharsets.UTF_8);
        final var creationDate = Instant.ofEpochMilli(event.timestamp());
        final var message = event.value();
        final var notification = mapper.toNotificationDto(message, orderId);
        notification.setCreationDate(creationDate);
        return service.saveNotification(notification);
    }

}
