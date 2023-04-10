package com.jmp.kafka.palmetto.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import com.jmp.kafka.palmetto.model.Product;

@Repository
public interface ProductRepository extends ReactiveMongoRepository<Product, String> {

    Mono<Boolean> existsByName(final String name);

    Mono<Product> findByName(final String name);

}
