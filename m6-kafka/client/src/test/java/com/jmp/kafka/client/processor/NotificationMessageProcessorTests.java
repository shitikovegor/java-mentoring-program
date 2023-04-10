package com.jmp.kafka.client.processor;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.mapper.NotificationMapper;
import com.jmp.kafka.client.service.NotificationService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationMessageProcessorTests {

    @Mock
    private NotificationService service;

    @Test
    void processMessage() {
        // given
        final var mapper = Mappers.getMapper(NotificationMapper.class);
        final var processor = new NotificationMessageProcessor(service, mapper);
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.COOKING);
        final var record = new ConsumerRecord<>("notifications", 0, 1L,
                TestMockData.ID, new NotificationMessageDto(StatusDto.COOKING));
        record.headers().add(KafkaHeaders.CORRELATION_ID, TestMockData.ID.getBytes(StandardCharsets.UTF_8));
        when(service.saveNotification(any())).thenReturn(Mono.just(notificationDto));
        // when
        StepVerifier.create(processor.processMessage(record))
                // then
                .expectNext(notificationDto)
                .verifyComplete();

    }

}
