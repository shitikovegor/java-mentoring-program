package com.jmp.kafka.courier.processor;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.jmp.kafka.courier.TestMockData;
import com.jmp.kafka.courier.dto.NotificationMessageDto;
import com.jmp.kafka.courier.dto.StatusDto;
import com.jmp.kafka.courier.service.OrderService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationMessageProcessorTests {

    @Mock
    private OrderService service;

    @Test
    void processMessage() {
        // given
        final var processor = new NotificationMessageProcessor(service);
        final var record = new ConsumerRecord<>("notifications", 0, 1L,
                TestMockData.ID, new NotificationMessageDto(StatusDto.READY));
        record.headers().add(KafkaHeaders.CORRELATION_ID, TestMockData.ID.getBytes(StandardCharsets.UTF_8));
        when(service.startDelivery(TestMockData.ID)).thenReturn(Mono.just(StatusDto.ON_DELIVERY));
        // when
        StepVerifier.create(processor.processMessage(record))
                // then
                .expectNext(StatusDto.ON_DELIVERY)
                .verifyComplete();
        verify(service).startDelivery(TestMockData.ID);
    }

    @Test
    void processMessage_NotReadyStatus() {
        // given
        final var processor = new NotificationMessageProcessor(service);
        final var record = new ConsumerRecord<>("notifications", 0, 1L,
                TestMockData.ID, new NotificationMessageDto(StatusDto.COOKING));
        record.headers().add(KafkaHeaders.CORRELATION_ID, TestMockData.ID.getBytes(StandardCharsets.UTF_8));
        // when
        StepVerifier.create(processor.processMessage(record))
                // then
                .expectComplete()
                .verify();
        verify(service, never()).startDelivery(any());
    }

}
