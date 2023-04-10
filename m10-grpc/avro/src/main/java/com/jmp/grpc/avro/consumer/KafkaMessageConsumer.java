package com.jmp.grpc.avro.consumer;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;

import com.jmp.grpc.avro.Message;
import com.jmp.grpc.avro.dto.MessageDto;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {

    private final ReactiveKafkaConsumerTemplate<String, Message> template;

    public Flux<MessageDto> consume() {
        return template
                .receiveAutoAck()
                .doOnNext(record -> log.info("Received key={}, value={} from topic={}, offset={}",
                        record.key(),
                        record.value(),
                        record.topic(),
                        record.offset()))
                .map(KafkaMessageConsumer::buildMessageDto)
                .doOnNext(event -> log.info("Successfully consumed event: {}", event))
                .doOnError(e -> log.error("Error while consuming : {}", e.getMessage(), e));
    }

    private static MessageDto buildMessageDto(final ConsumerRecord<String, Message> record) {
        return MessageDto.builder()
                .id(record.value().getId())
                .message(record.value().getText())
                .build();
    }

    @PostConstruct
    public void run() {
        consume().subscribe();
    }

}
