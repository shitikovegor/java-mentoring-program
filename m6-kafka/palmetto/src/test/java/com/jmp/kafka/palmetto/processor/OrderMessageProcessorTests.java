package com.jmp.kafka.palmetto.processor;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.jmp.kafka.palmetto.TestMockData;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.palmetto.mapper.OrderMapper;
import com.jmp.kafka.palmetto.service.OrderService;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderMessageProcessorTests {

    @Mock
    private OrderService service;

    @Test
    void processMessage() {
        // given
        final var mapper = Mappers.getMapper(OrderMapper.class);
        final var processor = new OrderMessageProcessor(service, mapper);
        final var record = new ConsumerRecord<>("notifications", 0, 1L,
                TestMockData.ID, TestMockData.buildOrderMessageDto());
        record.headers().add(KafkaHeaders.CORRELATION_ID, TestMockData.ID.getBytes(StandardCharsets.UTF_8));
        Mockito.when(service.startCooking(any())).thenReturn(Mono.just(StatusDto.COOKING));
        // when
        StepVerifier.create(processor.processMessage(record))
                // then
                .expectNext(StatusDto.COOKING)
                .verifyComplete();
    }

}
