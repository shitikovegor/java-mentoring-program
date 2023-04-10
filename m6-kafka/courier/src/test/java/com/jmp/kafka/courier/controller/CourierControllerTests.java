package com.jmp.kafka.courier.controller;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.jmp.kafka.courier.TestMockData;
import com.jmp.kafka.courier.dto.StatusDto;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CourierControllerTests {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    protected WebTestClient webTestClient;

    @SneakyThrows
    @Test
    void finishDelivery() {
        // given
        final var testConsumer = TestMockData.buildConsumer(kafkaContainer);
        testConsumer.subscribe(Collections.singletonList("notifications"));
        // when
        webTestClient.post()
                .uri("/api/v1/courier/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(StatusDto.DELIVERED));
        final var records = testConsumer.poll(Duration.of(3L, ChronoUnit.SECONDS));
        final var recordIterator = records.iterator();
        assertThat(recordIterator.hasNext()).isTrue();
        final var record = recordIterator.next();
        assertThat(record.key()).isEqualTo(TestMockData.ID);
        assertThat(record.value().getStatus()).isEqualTo(StatusDto.DELIVERED);
    }

}
