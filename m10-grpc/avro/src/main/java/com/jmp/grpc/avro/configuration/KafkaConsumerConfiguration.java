package com.jmp.grpc.avro.configuration;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import com.jmp.grpc.avro.Message;
import com.jmp.grpc.avro.consumer.KafkaMessageConsumer;

@Configuration
public class KafkaConsumerConfiguration {

    @Bean
    public ReceiverOptions<String, Message> receiverOptions(
            @Value("${application.kafka.topic}") final String topic,
            final KafkaProperties properties) {
        final ReceiverOptions<String, Message> basicReceiverOptions = ReceiverOptions
                .create(properties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Message> reactiveKafkaConsumerTemplate(
            final ReceiverOptions<String, Message> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public KafkaMessageConsumer kafkaOrderConsumer(
            final ReactiveKafkaConsumerTemplate template) {
        return new KafkaMessageConsumer(template);
    }

}
