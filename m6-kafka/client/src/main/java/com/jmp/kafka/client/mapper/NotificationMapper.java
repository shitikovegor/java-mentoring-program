package com.jmp.kafka.client.mapper;

import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.dto.NotificationMessageDto;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.model.Notification;
import com.jmp.kafka.client.model.Status;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    NotificationDto toNotificationDto(final Notification notification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderStatuses", ignore = true)
    Notification toNotification(final NotificationDto notificationDto);

    Status toStatus(final StatusDto statusDto);

    StatusDto toStatusDto(final Status status);

    @Mapping(target = "orderId", source = "orderId")
    NotificationDto toNotificationDto(final NotificationMessageDto message, final String orderId);

    @AfterMapping
    default void buildNotificationDto(@MappingTarget final NotificationDto.NotificationDtoBuilder dto,
            final Notification notification) {
        final var orderStatuses = notification.getOrderStatuses();
        if (!orderStatuses.isEmpty()) {
            orderStatuses.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .ifPresent(statusData -> {
                        dto.status(toStatusDto(statusData.getKey()));
                        dto.creationDate(statusData.getValue());
                    });
        }
    }

    @AfterMapping
    default void fillOrderStatuses(@MappingTarget final Notification.NotificationBuilder notification,
            final NotificationDto dto) {
        final var statuses = Map.of(toStatus(dto.getStatus()), dto.getCreationDate());
        notification.orderStatuses(statuses);
    }

}
