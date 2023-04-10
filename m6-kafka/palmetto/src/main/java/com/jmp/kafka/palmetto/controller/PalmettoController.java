package com.jmp.kafka.palmetto.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.jmp.kafka.palmetto.dto.ProductAddRequestDto;
import com.jmp.kafka.palmetto.dto.ProductDto;
import com.jmp.kafka.palmetto.dto.StatusDto;
import com.jmp.kafka.palmetto.service.OrderService;
import com.jmp.kafka.palmetto.service.ProductService;

@Validated
@RestController
@RequestMapping(path = "api/v1/palmetto", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PalmettoController {

    private final ProductService productService;

    private final OrderService orderService;

    @PostMapping(path = "/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductDto> addProduct(@RequestBody @NotNull @Valid final ProductAddRequestDto addRequest) {
        return productService.add(addRequest);
    }

    @GetMapping(path = "/products")
    public Flux<ProductDto> getProductsByNames(@RequestParam @NotEmpty final List<String> productNames) {
        return productService.findByProductNames(productNames);
    }

    @PostMapping(path = "/orders/{orderId}")
    public Mono<StatusDto> finishCooking(@PathVariable @NotEmpty final String orderId) {
        return orderService.finishCooking(orderId);
    }

}
