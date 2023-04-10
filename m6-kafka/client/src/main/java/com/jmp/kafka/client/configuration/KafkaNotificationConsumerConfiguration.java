package com.jmp.kafka.client.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.scheduler.Schedulers;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.configuration.AbstractKafkaConsumerConfiguration;
import com.jmp.kafka.configuration.properties.ConsumerProperties;
import com.jmp.kafka.consumer.KafkaMessageConsumer;
import com.jmp.kafka.processor.MessageProcessor;

@Configuration
@EnableConfigurationProperties(ConsumerProperties.class)
public class KafkaNotificationConsumerConfiguration extends AbstractKafkaConsumerConfiguration<NotificationMessageDto> {

    @Bean
    public KafkaMessageConsumer<NotificationMessageDto, NotificationDto> kafkaOrderConsumer(
            final ReactiveKafkaConsumerTemplate template,
            final MessageProcessor<NotificationMessageDto, NotificationDto> processor,
            final ConsumerProperties properties) {
        final var scheduler = Schedulers.newBoundedElastic(properties.getThreadsNumber(), properties.getMaxTasks(),
                properties.getTopic(), properties.getSchedulerTtl(), true);
        return new KafkaMessageConsumer<NotificationMessageDto, NotificationDto>(template, processor, scheduler);
    }

}
