package com.jmp.elastic.dto.search;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchRequestDto {

    private Map<EventSearchFieldDto, String> searchValuesByFields;

    private long page;

}
