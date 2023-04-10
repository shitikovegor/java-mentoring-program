package com.jmp.elastic.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private String id;

    private String title;

    private String eventType;

    private Instant eventDate;

    private String place;

    private String description;

    private List<String> subTopics;

}
