package com.jmp.kafka.client.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.jmp.kafka.api.palmetto.client.PalmettoApi;
import com.jmp.kafka.api.palmetto.dto.ProductDto;
import com.jmp.kafka.client.dto.OrderDto;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.client.dto.OrderRequestDto;
import com.jmp.kafka.client.dto.ProductOrderDto;
import com.jmp.kafka.client.mapper.OrderMapper;
import com.jmp.kafka.client.model.Order;
import com.jmp.kafka.client.repository.OrderRepository;
import com.jmp.kafka.producer.KafkaMessageProducer;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaMessageProducer<OrderMessageDto> producer;

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final PalmettoApi api;

    public Mono<OrderDto> addOrder(final OrderRequestDto requestDto) {
        final var order = mapper.toOrder(requestDto);
        final var productNames = requestDto.getBasket()
                .stream()
                .map(ProductOrderDto::getProductName)
                .toList();
        return api.getProductsByNames(productNames)
                .collectList()
                .flatMap(products -> saveOrder(order, products))
                .flatMap(this::sendOrder);
    }

    private Mono<OrderDto> sendOrder(final OrderDto orderDto) {
        final var message = mapper.toOrderMessageDto(orderDto);
        return producer.send(orderDto.getId(), message)
                .thenReturn(orderDto);
    }

    private Mono<OrderDto> saveOrder(final Order order, final List<ProductDto> products) {
        checkProductsInOrder(order, products);
        return repository.save(order)
                .map(mapper::toOrderDto);
    }

    public Mono<Boolean> existsOrder(final String orderId) {
        return repository.existsById(orderId);
    }

    private void checkProductsInOrder(final Order order, final List<ProductDto> products) {
        final var productsById = products.stream().collect(Collectors.toMap(ProductDto::getName, Function.identity()));
        final var basketToUpdate = order.getBasket().stream()
                .filter(productOrder -> productsById.containsKey(productOrder.getProductName()))
                .map(productOrder -> productOrder.toBuilder()
                        .productId(productsById.get(productOrder.getProductName()).getId()).build())
                .collect(Collectors.toList());
        order.setBasket(basketToUpdate);
    }

}
