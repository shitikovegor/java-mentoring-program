package com.jmp.kafka.palmetto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.testcontainers.containers.KafkaContainer;

import com.jmp.kafka.palmetto.dto.NotificationMessageDto;
import com.jmp.kafka.palmetto.dto.OrderDto;
import com.jmp.kafka.palmetto.dto.OrderMessageDto;
import com.jmp.kafka.palmetto.dto.ProductAddRequestDto;
import com.jmp.kafka.palmetto.dto.ProductDto;
import com.jmp.kafka.palmetto.dto.ProductOrderDto;
import com.jmp.kafka.palmetto.model.Product;

public class TestMockData {

    public static final String ID = "2a78c6dd-a2af-4e2e-aef8-53887c5d3d2e";

    public static final String PRODUCT_NAME = "Product";

    public static final Instant CREATION_DATE = Instant.parse("2023-01-22T16:00:00Z");

    public static OrderDto buildOrderDto() {
        return OrderDto.builder()
                .id(ID)
                .creationTime(CREATION_DATE)
                .basket(List.of(new ProductOrderDto(PRODUCT_NAME, 1))).build();
    }

    public static OrderMessageDto buildOrderMessageDto() {
        return OrderMessageDto.builder()
                .creationTime(CREATION_DATE)
                .basket(List.of(new ProductOrderDto(PRODUCT_NAME, 1)))
                .build();
    }

    public static ProductAddRequestDto buildProductRequest() {
        return new ProductAddRequestDto(TestMockData.PRODUCT_NAME);
    }

    public static ProductDto buildProductDto(final String productName) {
        return ProductDto.builder()
                .name(productName)
                .build();
    }

    public static Product buildProduct(final String productName) {
        return Product.builder()
                .name(productName)
                .build();
    }

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
