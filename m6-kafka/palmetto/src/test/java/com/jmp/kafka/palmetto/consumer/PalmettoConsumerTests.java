package com.jmp.kafka.palmetto.consumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.jmp.kafka.palmetto.TestMockData;
import com.jmp.kafka.palmetto.dto.OrderMessageDto;
import com.jmp.kafka.palmetto.dto.ProductOrderDto;
import com.jmp.kafka.palmetto.dto.StatusDto;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PalmettoConsumerTests {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    protected WebTestClient webTestClient;

    @SneakyThrows
    @Test
    void startCooking() {
        // given
        final var testConsumer = TestMockData.buildConsumer(kafkaContainer);
        testConsumer.subscribe(Collections.singletonList("notifications"));
        final var testProducer = buildProducer();
        final var message = new ProducerRecord<>("orders", TestMockData.ID,
                new OrderMessageDto(TestMockData.CREATION_DATE,
                        List.of(new ProductOrderDto(TestMockData.PRODUCT_NAME, 1))));
        final var idBytes = TestMockData.ID.getBytes(StandardCharsets.UTF_8);
        message.headers().add(KafkaHeaders.MESSAGE_KEY, idBytes)
                .add(KafkaHeaders.CORRELATION_ID, idBytes);
        testProducer.send(message);
        Thread.sleep(1000L);
        // then
        final var records = testConsumer.poll(Duration.of(3L, ChronoUnit.SECONDS));
        final var recordIterator = records.iterator();
        assertThat(recordIterator.hasNext()).isTrue();
        final var record = recordIterator.next();
        assertThat(record.key()).isEqualTo(TestMockData.ID);
        assertThat(record.value().getStatus()).isEqualTo(StatusDto.COOKING);
    }

    private Producer<String, OrderMessageDto> buildProducer() {
        final var factory = new DefaultKafkaProducerFactory<>(
                Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()),
                new StringSerializer(), new JsonSerializer<OrderMessageDto>());
        return factory.createProducer();
    }

}
