package com.jmp.kafka.configuration;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

public abstract class AbstractKafkaConsumerConfiguration<T> {

    @Bean
    public ReceiverOptions<String, T> receiverOptions(@Value("${application.kafka.consumer.topic}") final String topic,
            final KafkaProperties properties) {
        final ReceiverOptions<String, T> basicReceiverOptions = ReceiverOptions
                .create(properties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, T> reactiveKafkaConsumerTemplate(
            final ReceiverOptions<String, T> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

}
