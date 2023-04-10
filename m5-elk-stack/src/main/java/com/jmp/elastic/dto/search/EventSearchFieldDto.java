package com.jmp.elastic.dto.search;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventSearchFieldDto {

    TITLE("title"), EVENT_TYPE("eventType"), EVENT_DATE("eventDate");

    private final String name;

    @JsonValue
    public String toJson() {
        return getName();
    }

}
