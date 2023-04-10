package com.jmp.kafka.palmetto.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.jmp.kafka.exception.exception.BaseErrorCode;
import com.jmp.kafka.palmetto.dto.ProductAddRequestDto;
import com.jmp.kafka.palmetto.dto.ProductDto;
import com.jmp.kafka.palmetto.exception.PalmettoServiceException;
import com.jmp.kafka.palmetto.mapper.ProductMapper;
import com.jmp.kafka.palmetto.repository.ProductRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    private final ProductMapper mapper;

    public Mono<ProductDto> add(final ProductAddRequestDto addRequest) {
        return repository.existsByName(addRequest.getName())
                .flatMap(exists -> !exists ? saveProduct(addRequest) : Mono.error(
                        new PalmettoServiceException("Product with name" + addRequest.getName() + " exists",
                                HttpStatus.BAD_REQUEST, BaseErrorCode.BAD_REQUEST_ERROR_CODE)));
    }

    private Mono<ProductDto> saveProduct(final ProductAddRequestDto addRequest) {
        return repository.save(mapper.toProduct(addRequest))
                .map(mapper::toProductDto);
    }

    public Flux<ProductDto> findByProductNames(final List<String> productNames) {
        return Flux.fromIterable(productNames)
                .flatMap(repository::findByName)
                .map(mapper::toProductDto);
    }

}
