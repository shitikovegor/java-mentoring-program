package com.jmp.elastic.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jmp.elastic.model.Event;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDocument {

    @JsonProperty("_index")
    private String index;

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_score")
    private double score;

    @JsonProperty("_source")
    private Event source;

}
