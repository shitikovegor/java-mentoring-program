package com.jmp.kafka.client.service;

import java.util.HashMap;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.exception.ClientServiceException;
import com.jmp.kafka.client.mapper.NotificationMapper;
import com.jmp.kafka.client.model.Notification;
import com.jmp.kafka.client.repository.NotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;

    private final NotificationMapper mapper;

    private final OrderService orderService;

    public Mono<NotificationDto> saveNotification(final NotificationDto notificationDto) {
        return checkIfOrderExists(notificationDto.getOrderId())
                .flatMap(orderId -> repository.findByOrderId(orderId)
                        .flatMap(notification -> saveExistingNotification(notification, notificationDto))
                        .switchIfEmpty(saveNewNotification(notificationDto)))
                .map(mapper::toNotificationDto);
    }

    public Mono<NotificationDto> getOrderStatus(final String orderId) {
        return repository.findByOrderId(orderId)
                .map(mapper::toNotificationDto)
                .switchIfEmpty(Mono.error(ClientServiceException.createOrderNotFoundException(orderId)));
    }

    private Mono<String> checkIfOrderExists(final String orderId) {
        return orderService.existsOrder(orderId)
                .flatMap(exists -> exists ? Mono.just(orderId)
                        : Mono.error(ClientServiceException.createOrderNotFoundException(orderId)));
    }

    private Mono<Notification> saveExistingNotification(final Notification notification,
            final NotificationDto notificationDto) {
        final var orderStatuses = new HashMap<>(notification.getOrderStatuses());
        orderStatuses.put(mapper.toStatus(notificationDto.getStatus()), notificationDto.getCreationDate());
        notification.setOrderStatuses(orderStatuses);
        return repository.save(notification);
    }

    private Mono<Notification> saveNewNotification(final NotificationDto notificationDto) {
        final var notification = mapper.toNotification(notificationDto);
        notification.setId(UUID.randomUUID().toString());
        return repository.save(notification);
    }

}
