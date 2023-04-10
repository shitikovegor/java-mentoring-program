package com.jmp.elastic.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jmp.elastic.client.EventClient;
import com.jmp.elastic.dto.event.EventDto;
import com.jmp.elastic.dto.event.EventPatchRequestDto;
import com.jmp.elastic.dto.event.EventRequestDto;
import com.jmp.elastic.dto.search.EventSearchRequestDto;
import com.jmp.elastic.exception.ErrorCode;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.mapper.EventMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventClient client;

    private final EventMapper mapper;

    public void createEvent(final EventRequestDto request) {
        client.createEvent(mapper.toEvent(request));
    }

    public EventDto getEvent(final String id) {
        final var event = client.getEvent(id)
                .orElseThrow(() -> {
                    log.error("Event with id {} not found", id);
                    throw new EventException("Event with id " + id + " not found", ErrorCode.EVENT_NOT_FOUND,
                            HttpStatus.NOT_FOUND);
                });
        return mapper.toEventDto(event);
    }

    public void updateEvent(final String id, final EventPatchRequestDto eventRequest) {
        checkIfEventExists(id);
        client.updateEvent(id, mapper.toEvent(eventRequest));
    }

    public void updateEvent(final String id, final EventRequestDto eventRequest) {
        checkIfEventExists(id);
        client.updateEvent(id, mapper.toEvent(eventRequest));
    }

    public void deleteEvent(final String id) {
        checkIfEventExists(id);
        client.deleteEvent(id);
    }

    public List<EventDto> searchEvents(final EventSearchRequestDto searchRequest) {
        final var events = client.searchEvents(mapper.toSearchRequest(searchRequest));
        return mapper.toEventDtos(events);
    }

    public void deleteEventByTitle(final String title) {
        client.deleteEventsByTitle(title);
    }

    private void checkIfEventExists(final String id) {
        if (!client.existsEvent(id)) {
            log.error("Event with id {} not found", id);
            throw new EventException("Event with id " + id + " not found", ErrorCode.EVENT_NOT_FOUND,
                    HttpStatus.NOT_FOUND);
        }
    }

}
