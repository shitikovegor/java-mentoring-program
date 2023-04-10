package com.jmp.elastic.client;

import java.util.List;
import java.util.Optional;

import com.jmp.elastic.model.Event;
import com.jmp.elastic.model.EventSearchRequest;

public interface EventClient {

    void createEvent(Event event);

    Optional<Event> getEvent(String id);

    void updateEvent(final String id, Event eventToUpdate);

    boolean existsEvent(String id);

    void deleteEvent(String id);

    List<Event> searchEvents(EventSearchRequest searchRequest);

    void deleteEventsByTitle(String title);

}
