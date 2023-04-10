package com.jmp.elastic.dto.event;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {

    @NotNull
    private String title;

    @NotNull
    private EventTypeDto eventType;

    @NotNull
    private Instant eventDate;

    @NotNull
    private String place;

    @NotNull
    private String description;

    @NotEmpty
    private List<String> subTopics;

}
