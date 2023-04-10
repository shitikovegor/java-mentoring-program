package com.jmp.kafka.courier;

import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.containers.KafkaContainer;

import com.jmp.kafka.courier.dto.NotificationMessageDto;

public class TestMockData {

    public static final String ID = "2a78c6dd-a2af-4e2e-aef8-53887c5d3d2e";

    public static Consumer<String, NotificationMessageDto> buildConsumer(final KafkaContainer kafkaContainer) {
        final var factory = new DefaultKafkaConsumerFactory<>(
                Map.of(ConsumerConfig.GROUP_ID_CONFIG, "notification-test-consumer",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()),
                new StringDeserializer(),
                new JsonDeserializer<>(NotificationMessageDto.class, false));
        return factory.createConsumer();
    }

}
