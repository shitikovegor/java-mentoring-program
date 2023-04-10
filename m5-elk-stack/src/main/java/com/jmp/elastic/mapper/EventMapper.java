package com.jmp.elastic.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.jmp.elastic.dto.event.EventDto;
import com.jmp.elastic.dto.event.EventPatchRequestDto;
import com.jmp.elastic.dto.event.EventRequestDto;
import com.jmp.elastic.dto.search.EventSearchRequestDto;
import com.jmp.elastic.model.Event;
import com.jmp.elastic.model.EventSearchRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {

    EventDto toEventDto(final Event event);

    List<EventDto> toEventDtos(final List<Event> books);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventType", expression = "java(eventRequestDto.getEventType().getName())")
    Event toEvent(final EventRequestDto eventRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventType", ignore = true)
    Event toEvent(final EventPatchRequestDto eventPatchRequestDto);

    EventSearchRequest toSearchRequest(final EventSearchRequestDto eventSearchRequestDto);

    @AfterMapping
    default void mapEventType(@MappingTarget final Event.EventBuilder event, final EventPatchRequestDto request) {
        if (request.getEventType() != null) {
            event.eventType(request.getEventType().getName());
        }
    }

}
