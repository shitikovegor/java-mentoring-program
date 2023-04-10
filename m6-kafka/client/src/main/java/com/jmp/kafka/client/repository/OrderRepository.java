package com.jmp.kafka.client.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.jmp.kafka.client.model.Order;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

}
