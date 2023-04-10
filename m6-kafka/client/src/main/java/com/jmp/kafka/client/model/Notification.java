package com.jmp.kafka.client.model;

import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Notification {

    @Id
    private String id;

    private String orderId;

    private Map<Status, Instant> orderStatuses;

}
