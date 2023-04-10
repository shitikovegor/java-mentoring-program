package com.jmp.kafka.producer;

import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

/*
 * Kafka producer that sends to message with type <T>.
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageProducer<T> {

    private final String topic;

    private final ReactiveKafkaProducerTemplate<String, T> template;

    public Mono<SenderResult<Void>> send(final String orderId, final T message) {
        final var record = new ProducerRecord<>(topic, orderId, message);
        record.headers()
                .add(KafkaHeaders.MESSAGE_KEY, orderId.getBytes(StandardCharsets.UTF_8))
                .add(KafkaHeaders.CORRELATION_ID, orderId.getBytes(StandardCharsets.UTF_8));
        return template.send(record)
                .doOnSuccess(senderResult -> log.info("Sent {} offset : {}", message,
                        senderResult.recordMetadata().offset()))
                .doOnError(e -> log.error("Send of order {} is failed", message, e));
    }

}
