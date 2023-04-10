package com.jmp.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import reactor.core.publisher.Mono;

/*
 * Processor that processes event <T> and returns object <U>.
 */
public interface MessageProcessor<T, U> {

    Mono<U> processMessage(final ConsumerRecord<String, T> message);

}
