package com.jmp.kafka.courier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import com.jmp.kafka.courier.TestMockData;
import com.jmp.kafka.courier.dto.NotificationMessageDto;
import com.jmp.kafka.courier.dto.StatusDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private KafkaMessageProducer<NotificationMessageDto> producer;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(producer);
    }

    @Test
    void startDelivery() {
        // given
        final var senderResult = Mockito.mock(SenderResult.class);
        when(producer.send(eq(TestMockData.ID), any())).thenReturn(Mono.just(senderResult));
        // when
        StepVerifier.create(service.startDelivery(TestMockData.ID))
                // then
                .expectNext(StatusDto.ON_DELIVERY)
                .verifyComplete();
    }

    @Test
    void finishDelivery() {
        // given
        final var senderResult = Mockito.mock(SenderResult.class);
        when(producer.send(eq(TestMockData.ID), any())).thenReturn(Mono.just(senderResult));
        // when
        StepVerifier.create(service.finishDelivery(TestMockData.ID))
                // then
                .expectNext(StatusDto.DELIVERED)
                .verifyComplete();
    }

}
