package com.jmp.elastic.dto.event;

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
public class EventPatchRequestDto {

    private String title;

    private EventTypeDto eventType;

    private Instant eventDate;

    private String place;

    private String description;

    private List<String> subTopics;

}
