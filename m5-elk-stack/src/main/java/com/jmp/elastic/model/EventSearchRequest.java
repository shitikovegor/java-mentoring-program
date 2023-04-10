package com.jmp.elastic.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchRequest {

    private Map<EventSearchField, String> searchValuesByFields;

    private long page;

}
