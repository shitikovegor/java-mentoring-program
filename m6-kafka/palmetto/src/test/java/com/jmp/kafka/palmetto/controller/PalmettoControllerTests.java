package com.jmp.kafka.palmetto.controller;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmp.kafka.palmetto.TestMockData;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.palmetto.model.Product;
import com.jmp.kafka.palmetto.repository.ProductRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Testcontainers
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PalmettoControllerTests {

    private static final String PATH = "/api/v1/palmetto";

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @MockBean
    private ProductRepository repository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    protected WebTestClient webTestClient;

    @SneakyThrows
    @Test
    void addProduct() {
        // given
        final var request = TestMockData.buildProductRequest();
        final var productDto = TestMockData.buildProductDto(TestMockData.PRODUCT_NAME);
        when(repository.existsByName(TestMockData.PRODUCT_NAME)).thenReturn(Mono.just(Boolean.FALSE));
        doAnswer(invocation -> {
            final var argument = invocation.<Product>getArgument(0);
            productDto.setId(argument.getId());
            return Mono.just(argument);
        }).when(repository).save(any());
        // when
        webTestClient.post()
                .uri(PATH + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(mapper.writeValueAsString(productDto));
    }

    @SneakyThrows
    @Test
    void getProductsByNames() {
        // given
        final var productDto = TestMockData.buildProductDto(TestMockData.PRODUCT_NAME);
        final var product = TestMockData.buildProduct(TestMockData.PRODUCT_NAME);
        when(repository.findByName(TestMockData.PRODUCT_NAME)).thenReturn(Mono.just(product));
        // when
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH + "/products")
                        .queryParam("productNames", TestMockData.PRODUCT_NAME)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(List.of(productDto)));
    }

    @SneakyThrows
    @Test
    void finishCooking() {
        // given
        final var testConsumer = TestMockData.buildConsumer(kafkaContainer);
        testConsumer.subscribe(Collections.singletonList("notifications"));
        // when
        webTestClient.post()
                .uri(PATH + "/orders/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(StatusDto.READY));
        final var records = testConsumer.poll(Duration.of(3L, ChronoUnit.SECONDS));
        final var recordIterator = records.iterator();
        assertThat(recordIterator.hasNext()).isTrue();
        final var record = recordIterator.next();
        assertThat(record.key()).isEqualTo(TestMockData.ID);
        assertThat(record.value().getStatus()).isEqualTo(StatusDto.READY);
    }

}
