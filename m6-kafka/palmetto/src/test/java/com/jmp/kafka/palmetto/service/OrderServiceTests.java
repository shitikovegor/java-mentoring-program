package com.jmp.kafka.palmetto.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import com.jmp.kafka.palmetto.TestMockData;
import com.jmp.kafka.palmetto.dto.NotificationMessageDto;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private KafkaMessageProducer<NotificationMessageDto> producer;

    @Mock
    private SenderResult senderResult;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(producer);
    }

    @Test
    void startCooking() {
        // given
        final var orderDto = TestMockData.buildOrderDto();
        when(producer.send(eq(TestMockData.ID), any())).thenReturn(Mono.just(senderResult));
        // when
        StepVerifier.create(service.startCooking(orderDto))
                // then
                .expectNext(StatusDto.COOKING)
                .verifyComplete();
    }

    @Test
    void finishCooking() {
        // given
        when(producer.send(eq(TestMockData.ID), any())).thenReturn(Mono.just(senderResult));
        // when
        StepVerifier.create(service.finishCooking(TestMockData.ID))
                // then
                .expectNext(StatusDto.READY)
                .verifyComplete();
    }

}
