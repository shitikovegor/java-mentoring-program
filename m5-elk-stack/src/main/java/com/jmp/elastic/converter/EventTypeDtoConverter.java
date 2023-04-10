package com.jmp.elastic.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.jmp.elastic.dto.event.EventTypeDto;

@Component
public class EventTypeDtoConverter implements Converter<String, EventTypeDto> {

    @Override
    public EventTypeDto convert(final String value) {
        return EventTypeDto.fromName(value);
    }

}
