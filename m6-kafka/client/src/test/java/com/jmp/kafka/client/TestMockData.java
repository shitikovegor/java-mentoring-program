package com.jmp.kafka.client;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.jmp.kafka.client.dto.NotificationDto;
import com.jmp.kafka.client.dto.OrderDto;
import com.jmp.kafka.client.dto.OrderMessageDto;
import com.jmp.kafka.client.dto.ProductOrderDto;
import com.jmp.kafka.client.dto.StatusDto;
import com.jmp.kafka.client.model.Notification;
import com.jmp.kafka.client.model.Order;
import com.jmp.kafka.client.model.ProductOrder;
import com.jmp.kafka.client.model.Status;

public class TestMockData {

    public static final String ID = "2a78c6dd-a2af-4e2e-aef8-53887c5d3d2e";

    public static final String PRODUCT_NAME = "Product";

    public static final String NAME = "Name";

    public static final String PHONE = "375292223344";

    public static final Instant CREATION_DATE = Instant.parse("2023-01-22T16:00:00Z");

    public static Order buildOrder() {
        return Order.builder()
                .id(ID)
                .name(NAME)
                .phone(PHONE)
                .creationTime(CREATION_DATE)
                .basket(List.of(new ProductOrder(ID, PRODUCT_NAME, 1))).build();
    }

    public static OrderDto buildOrderDto() {
        return OrderDto.builder()
                .id(ID)
                .name(NAME)
                .phone(PHONE)
                .creationTime(CREATION_DATE)
                .basket(List.of(new ProductOrderDto(PRODUCT_NAME, 1))).build();
    }

    public static OrderMessageDto buildOrderMessageDto() {
        return OrderMessageDto.builder()
                .creationTime(CREATION_DATE)
                .basket(List.of(new ProductOrderDto(PRODUCT_NAME, 1)))
                .build();
    }

    public static NotificationDto buildNotificationDto(final StatusDto status) {
        return NotificationDto.builder()
                .orderId(ID)
                .status(status)
                .creationDate(CREATION_DATE)
                .build();
    }

    public static Notification buildNotification(final Map<Status, Instant> statuses) {
        return Notification.builder()
                .orderId(ID)
                .orderStatuses(statuses)
                .build();
    }

}
