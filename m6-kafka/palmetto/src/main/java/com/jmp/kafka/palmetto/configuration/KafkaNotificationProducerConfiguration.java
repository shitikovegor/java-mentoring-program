package com.jmp.kafka.palmetto.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;

import com.jmp.kafka.configuration.AbstractKafkaProducerConfiguration;
import com.jmp.kafka.palmetto.dto.NotificationMessageDto;
import com.jmp.kafka.producer.KafkaMessageProducer;

@Configuration
public class KafkaNotificationProducerConfiguration extends AbstractKafkaProducerConfiguration<NotificationMessageDto> {

    @Bean
    public KafkaMessageProducer<NotificationMessageDto> kafkaNotificationProducer(
            @Value("${application.kafka.producer.topic}") final String topic,
            final ReactiveKafkaProducerTemplate template) {
        return new KafkaMessageProducer<NotificationMessageDto>(topic, template);
    }

}
