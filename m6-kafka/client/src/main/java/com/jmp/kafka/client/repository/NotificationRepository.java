package com.jmp.kafka.client.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import com.jmp.kafka.client.model.Notification;

@Repository
public interface NotificationRepository extends ReactiveMongoRepository<Notification, String> {

    Mono<Notification> findByOrderId(final String orderId);

}
