package com.jmp.grpc.avro.producer;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderResult;

import com.jmp.grpc.avro.Message;
import com.jmp.grpc.avro.dto.MessageRequestDto;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageProducer {

    private final String topic;

    private final ReactiveKafkaProducerTemplate<String, Message> template;

    public Mono<SenderResult<Void>> send(final MessageRequestDto request) {
        final var id = UUID.randomUUID().toString();
        final var message = Message.newBuilder()
                .setId(id)
                .setText(request.getMessage())
                .build();
        return template.send(topic, id, message)
                .doOnSuccess(senderResult -> log.info("Sent {} ", message))
                .doOnError(e -> log.error("Send of order {} is failed", message, e));
    }

}
