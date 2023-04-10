package com.jmp.kafka.client.model;

import java.time.Instant;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Order {

    @Id
    private String id;

    private String name;

    private String phone;

    private Instant creationTime;

    private List<ProductOrder> basket;

}
