package com.jmp.kafka.client.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import com.jmp.kafka.client.TestMockData;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.exception.ClientServiceException;
import com.jmp.kafka.client.mapper.NotificationMapper;
import com.jmp.kafka.client.model.Status;
import com.jmp.kafka.client.repository.NotificationRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private NotificationRepository repository;

    @Mock
    private OrderService orderService;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        final var mapper = Mappers.getMapper(NotificationMapper.class);
        service = new NotificationService(repository, mapper, orderService);
    }

    @Test
    void saveNotification_NewNotification() {
        // given
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.ACCEPTED);
        final var notification = TestMockData.buildNotification(Map.of(Status.ACCEPTED, TestMockData.CREATION_DATE));
        when(orderService.existsOrder(TestMockData.ID)).thenReturn(Mono.just(Boolean.TRUE));
        when(repository.findByOrderId(TestMockData.ID)).thenReturn(Mono.empty());
        when(repository.save(any())).thenReturn(Mono.just(notification));
        // when
        StepVerifier.create(service.saveNotification(notificationDto))
                // then
                .expectNext(notificationDto)
                .verifyComplete();
    }

    @Test
    void saveNotification_ExistingNotification() {
        // given
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.ACCEPTED);
        final var expected = TestMockData.buildNotificationDto(StatusDto.COOKING);
        final var statuses = new HashMap<Status, Instant>();
        statuses.put(Status.ACCEPTED, TestMockData.CREATION_DATE.minusSeconds(50));
        final var existingNotification = TestMockData.buildNotification(statuses);
        final var savedNotification = TestMockData.buildNotification(
                Map.of(Status.ACCEPTED, TestMockData.CREATION_DATE.minusSeconds(50),
                        Status.COOKING, TestMockData.CREATION_DATE));
        when(orderService.existsOrder(TestMockData.ID)).thenReturn(Mono.just(Boolean.TRUE));
        when(repository.findByOrderId(TestMockData.ID)).thenReturn(Mono.just(existingNotification));
        when(repository.save(any())).thenReturn(Mono.just(savedNotification));
        // when
        StepVerifier.create(service.saveNotification(notificationDto))
                // then
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void saveNotification_NotFound() {
        // given
        final var notificationDto = TestMockData.buildNotificationDto(StatusDto.ACCEPTED);
        when(orderService.existsOrder(TestMockData.ID)).thenReturn(Mono.just(Boolean.FALSE));
        // when
        StepVerifier.create(service.saveNotification(notificationDto))
                // then
                .expectError(ClientServiceException.class)
                .verify();
        verify(repository, never()).save(any());
    }

    @Test
    void getOrderStatus() {
        // given
        final var expected = TestMockData.buildNotificationDto(StatusDto.ACCEPTED);
        final var notification = TestMockData.buildNotification(Map.of(Status.ACCEPTED, TestMockData.CREATION_DATE));
        when(repository.findByOrderId(TestMockData.ID)).thenReturn(Mono.just(notification));
        // when
        StepVerifier.create(service.getOrderStatus(TestMockData.ID))
                // then
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getOrderStatus_NotFound() {
        // given
        when(repository.findByOrderId(TestMockData.ID)).thenReturn(Mono.empty());
        // when
        StepVerifier.create(service.getOrderStatus(TestMockData.ID))
                // then
                .expectError(ClientServiceException.class)
                .verify();
    }

}
