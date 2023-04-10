package com.jmp.kafka.client.producer;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaOrderProducerTests {

    @Mock
    private ReactiveKafkaProducerTemplate<String, OrderMessageDto> template;

    @Test
    void send() {
        // given
        final var topic = "orders";
        final var message = TestMockData.buildOrderMessageDto();
        final var producer = new KafkaMessageProducer<>(topic, template);
        final var record = new ProducerRecord<>(topic, TestMockData.ID, message);
        final var senderResult = Mockito.mock(SenderResult.class, Answers.RETURNS_DEEP_STUBS);
        when(senderResult.recordMetadata().offset()).thenReturn(1L);
        record.headers()
                .add(KafkaHeaders.MESSAGE_KEY, TestMockData.ID.getBytes(StandardCharsets.UTF_8))
                .add(KafkaHeaders.CORRELATION_ID, TestMockData.ID.getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(producer, "topic", topic);
        when(template.send(record)).thenReturn(Mono.just(senderResult));
        // when
        StepVerifier.create(producer.send(TestMockData.ID, message))
                // then
                .expectNextCount(1)
                .verifyComplete();
        verify(template).send(record);
    }

}
