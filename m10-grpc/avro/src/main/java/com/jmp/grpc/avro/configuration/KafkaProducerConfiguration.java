package com.jmp.grpc.avro.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import com.jmp.grpc.avro.Message;
import com.jmp.grpc.avro.producer.KafkaMessageProducer;

@Configuration
public class KafkaProducerConfiguration {

    @Bean
    public ReactiveKafkaProducerTemplate<String, Message> reactiveKafkaProducerTemplate(
            final KafkaProperties properties) {
        final var producerProperties = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(producerProperties));
    }

    @Bean
    public KafkaMessageProducer kafkaNotificationProducer(
            @Value("${application.kafka.topic}") final String topic,
            final ReactiveKafkaProducerTemplate template) {
        return new KafkaMessageProducer(topic, template);
    }

}
