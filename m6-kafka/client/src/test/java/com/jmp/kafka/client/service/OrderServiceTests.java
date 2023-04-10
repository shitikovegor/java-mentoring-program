package com.jmp.kafka.client.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.jmp.kafka.api.palmetto.client.PalmettoApi;
import com.jmp.kafka.api.palmetto.dto.ProductDto;
import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.client.dto.OrderRequestDto;
import com.jmp.kafka.client.dto.ProductOrderDto;
import com.jmp.kafka.client.mapper.OrderMapper;
import com.jmp.kafka.client.repository.OrderRepository;
import com.jmp.kafka.producer.KafkaMessageProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @Mock
    private PalmettoApi api;

    @Mock
    private OrderRepository repository;

    @Mock
    private KafkaMessageProducer<OrderMessageDto> producer;

    private OrderService service;

    @BeforeEach
    void setUp() {
        final var mapper = Mappers.getMapper(OrderMapper.class);
        service = new OrderService(producer, repository, mapper, api);
    }

    @Test
    void addOrder_SuccessfullyAdded() {
        // given
        final var order = TestMockData.buildOrder();
        final var expected = TestMockData.buildOrderDto();
        final var request = new OrderRequestDto(TestMockData.NAME, TestMockData.PHONE,
                List.of(new ProductOrderDto(TestMockData.PRODUCT_NAME, 1)));
        when(api.getProductsByNames(List.of(TestMockData.PRODUCT_NAME))).thenReturn(
                Flux.just(new ProductDto(TestMockData.ID, TestMockData.PRODUCT_NAME)));
        when(repository.save(any())).thenReturn(Mono.just(order));
        when(producer.send(any(), any())).thenReturn(Mono.empty());
        // when
        StepVerifier.create(service.addOrder(request))
                // then
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void addOrder_OneValidProductAndOneInvalid_OneSaved() {
        // given
        final var order = TestMockData.buildOrder();
        final var expected = TestMockData.buildOrderDto();
        final var request = new OrderRequestDto(TestMockData.NAME, TestMockData.PHONE,
                List.of(new ProductOrderDto(TestMockData.PRODUCT_NAME, 1), new ProductOrderDto("Product1", 1)));
        when(api.getProductsByNames(List.of(TestMockData.PRODUCT_NAME, "Product1")))
                .thenReturn(Flux.just(new ProductDto(TestMockData.ID, TestMockData.PRODUCT_NAME)));
        when(repository.save(any())).thenReturn(Mono.just(order));
        when(producer.send(any(), any())).thenReturn(Mono.empty());
        // when
        StepVerifier.create(service.addOrder(request))
                // then
                .expectNext(expected)
                .verifyComplete();
    }

}
