package com.jmp.elastic.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventSearchField {

    TITLE("title"), EVENT_TYPE("eventType"), EVENT_DATE("eventDate");

    private final String name;

}
