package com.jmp.kafka.client.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.configuration.AbstractKafkaProducerConfiguration;
import com.jmp.kafka.producer.KafkaMessageProducer;

@Configuration
public class KafkaOrderProducerConfiguration extends AbstractKafkaProducerConfiguration<OrderMessageDto> {

    @Bean
    public KafkaMessageProducer<OrderMessageDto> kafkaNotificationProducer(
            @Value("${application.kafka.producer.topic}") final String topic,
            final ReactiveKafkaProducerTemplate template) {
        return new KafkaMessageProducer<OrderMessageDto>(topic, template);
    }

}
