package com.jmp.kafka.api.palmetto.client;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import com.jmp.kafka.api.palmetto.dto.ProductDto;

@RequiredArgsConstructor
public class PalmettoApi {

    private final WebClient webClient;

    public Flux<ProductDto> getProductsByNames(final List<String> productNames) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/v1/palmetto/products")
                        .queryParam("productNames", productNames)
                        .build())
                .retrieve()
                .bodyToFlux(ProductDto.class);
    }

}
