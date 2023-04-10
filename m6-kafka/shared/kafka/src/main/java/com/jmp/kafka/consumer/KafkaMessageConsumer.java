package com.jmp.kafka.consumer;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import com.jmp.kafka.processor.MessageProcessor;

/*
 * Consumer that receives message <T> from kafka and returns <U>.
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer<T, U> {

    private final ReactiveKafkaConsumerTemplate<String, T> template;

    private final MessageProcessor<T, U> processor;

    private final Scheduler scheduler;

    public Flux<U> consume() {
        return template
                .receiveAutoAck()
                .groupBy(ConsumerRecord::partition)
                .flatMap(partitionFlux -> partitionFlux.publishOn(scheduler)
                        .doOnNext(record -> log.info("Received key={}, value={} from topic={}, offset={}",
                                record.key(),
                                record.value(),
                                record.topic(),
                                record.offset()))
                        .flatMap(processor::processMessage)
                        .doOnNext(event -> log.info("Successfully consumed event: {}", event))
                        .doOnError(e -> log.error("Error while consuming : {}", e.getMessage(), e)));
    }

    @PostConstruct
    public void run() {
        consume().subscribe();
    }

}
