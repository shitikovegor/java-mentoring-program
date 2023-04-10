package com.jmp.kafka.client.controller;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.jmp.kafka.api.palmetto.client.PalmettoApi;
import com.jmp.kafka.api.palmetto.dto.ProductDto;
import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.client.dto.OrderRequestDto;
import com.jmp.kafka.client.dto.ProductOrderDto;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.model.Notification;
import com.jmp.kafka.client.model.Status;
import com.jmp.kafka.client.repository.NotificationRepository;
import com.jmp.kafka.client.repository.OrderRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@Testcontainers
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientControllerTests {

    private static final String PATH = "/api/v1/client";

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private PalmettoApi api;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    protected WebTestClient webTestClient;

    @SneakyThrows
    @Test
    void addOrder() {
        // given
        final var testConsumer = buildConsumer();
        testConsumer.subscribe(Collections.singletonList("orders"));
        final var basket = List.of(new ProductOrderDto(TestMockData.PRODUCT_NAME, 1));
        final var requestDto = OrderRequestDto.builder()
                .name(TestMockData.NAME)
                .phone(TestMockData.PHONE)
                .basket(basket)
                .build();
        final var order = TestMockData.buildOrder();
        final var productDto = ProductDto.builder()
                .id(TestMockData.ID)
                .name(TestMockData.NAME)
                .build();
        when(api.getProductsByNames(List.of(TestMockData.PRODUCT_NAME))).thenReturn(Flux.just(productDto));
        when(orderRepository.save(any())).thenReturn(Mono.just(order));
        // when
        webTestClient.post()
                .uri(PATH + "/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(mapper.writeValueAsString(TestMockData.buildOrderDto()));
        final var records = testConsumer.poll(Duration.of(3L, ChronoUnit.SECONDS));
        final var recordIterator = records.iterator();
        assertThat(recordIterator.hasNext()).isTrue();
        final var record = recordIterator.next();
        assertThat(record.key()).isEqualTo(TestMockData.ID);
        assertThat(record.value().getBasket()).isEqualTo(basket);
    }

    @SneakyThrows
    @Test
    void getOrderStatus() {
        // given
        final var testProducer = buildProducer();
        final var record = new ProducerRecord<>("notifications", TestMockData.ID,
                new NotificationMessageDto(StatusDto.COOKING));
        final var idBytes = TestMockData.ID.getBytes(StandardCharsets.UTF_8);
        record.headers().add(KafkaHeaders.MESSAGE_KEY, idBytes)
                .add(KafkaHeaders.CORRELATION_ID, idBytes);
        testProducer.send(record);
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.COOKING);
        final var notification = TestMockData.buildNotification(Map.of(Status.ACCEPTED, TestMockData.CREATION_DATE));
        when(orderRepository.existsById(TestMockData.ID)).thenReturn(Mono.just(Boolean.TRUE));
        when(notificationRepository.findByOrderId(TestMockData.ID)).thenReturn(Mono.just(notification));
        doAnswer(invocation -> {
            final var argument = invocation.<Notification>getArgument(0);
            notification.setOrderStatuses(argument.getOrderStatuses());
            notificationDto.setCreationDate(argument.getOrderStatuses().get(Status.COOKING));
            return Mono.just(argument);
        }).when(notificationRepository).save(any(Notification.class));
        // when
        Thread.sleep(1000L);
        webTestClient.get()
                .uri(PATH + "/notifications/" + TestMockData.ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // then
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(notificationDto));
    }

    private Consumer<String, OrderMessageDto> buildConsumer() {
        final var factory = new DefaultKafkaConsumerFactory<>(
                Map.of(ConsumerConfig.GROUP_ID_CONFIG, "order-test-consumer",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()),
                new StringDeserializer(),
                new JsonDeserializer<>(OrderMessageDto.class, false));
        return factory.createConsumer();
    }

    private Producer<String, NotificationMessageDto> buildProducer() {
        final var factory = new DefaultKafkaProducerFactory<>(
                Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()),
                new StringSerializer(), new JsonSerializer<NotificationMessageDto>());
        return factory.createProducer();
    }

}
