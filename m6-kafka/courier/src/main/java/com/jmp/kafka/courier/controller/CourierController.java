package com.jmp.kafka.courier.controller;

import javax.validation.constraints.NotEmpty;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.jmp.kafka.courier.dto.StatusDto;
import com.jmp.kafka.courier.service.OrderService;

@Validated
@RestController
@RequestMapping(path = "api/v1/courier", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CourierController {

    private final OrderService orderService;

    @PostMapping(path = "/{orderId}")
    public Mono<StatusDto> finishDelivery(@PathVariable @NotEmpty final String orderId) {
        return orderService.finishDelivery(orderId);
    }

}
