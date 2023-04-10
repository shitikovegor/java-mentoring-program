package com.jmp.kafka.client.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.dto.OrderDto;
import com.jmp.kafka.client.dto.OrderRequestDto;
import com.jmp.kafka.client.service.NotificationService;
import com.jmp.kafka.client.service.OrderService;

@Validated
@RestController
@RequestMapping(path = "api/v1/client", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ClientController {

    private final NotificationService notificationService;

    private final OrderService orderService;

    @PostMapping(path = "/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDto> addOrder(@RequestBody @NotNull @Valid final OrderRequestDto requestDto) {
        return orderService.addOrder(requestDto);
    }

    @GetMapping(path = "/notifications/{orderId}")
    public Mono<NotificationDto> getOrderStatus(@PathVariable @NotEmpty final String orderId) {
        return notificationService.getOrderStatus(orderId);
    }

}
