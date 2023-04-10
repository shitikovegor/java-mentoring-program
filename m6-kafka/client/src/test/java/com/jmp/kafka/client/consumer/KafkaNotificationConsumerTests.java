package com.jmp.kafka.client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.processor.NotificationMessageProcessor;
import com.jmp.kafka.consumer.KafkaMessageConsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaNotificationConsumerTests {

    @Mock
    private ReactiveKafkaConsumerTemplate<String, NotificationMessageDto> template;

    @Mock
    private NotificationMessageProcessor processor;

    @Test
    void consume() {
        // given
        final var scheduler = Schedulers.newBoundedElastic(2, 10, "notifications", 60, true);
        final var message = NotificationMessageDto.builder()
                .status(StatusDto.ACCEPTED)
                .build();
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.ACCEPTED);
        final var consumer = new KafkaMessageConsumer<>(template, processor, scheduler);
        when(template.receiveAutoAck()).thenReturn(
                Flux.just(new ConsumerRecord<>("notifications", 1, 1L, null, message)));
        when(processor.processMessage(any())).thenReturn(Mono.just(notificationDto));
        // when
        StepVerifier.create(consumer.consume())
                // then
                .expectNext(notificationDto)
                .verifyComplete();
        verify(template).receiveAutoAck();
        verify(processor).processMessage(any());
    }

}
