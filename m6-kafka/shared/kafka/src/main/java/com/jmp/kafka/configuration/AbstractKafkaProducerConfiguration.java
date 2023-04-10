package com.jmp.kafka.configuration;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

public abstract class AbstractKafkaProducerConfiguration<T> {

    @Bean
    public ReactiveKafkaProducerTemplate<String, T> reactiveKafkaProducerTemplate(final KafkaProperties properties) {
        final var producerProperties = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerProperties));
    }

}
