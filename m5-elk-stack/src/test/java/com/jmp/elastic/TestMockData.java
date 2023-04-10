package com.jmp.elastic;

import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;

import com.jmp.elastic.dto.event.EventDto;
import com.jmp.elastic.dto.event.EventPatchRequestDto;
import com.jmp.elastic.dto.event.EventRequestDto;
import com.jmp.elastic.dto.event.EventTypeDto;
import com.jmp.elastic.dto.index.IndexMappingDto;
import com.jmp.elastic.dto.index.IndexRequestDto;
import com.jmp.elastic.dto.search.EventSearchFieldDto;
import com.jmp.elastic.dto.search.EventSearchRequestDto;
import com.jmp.elastic.model.Event;
import com.jmp.elastic.model.EventSearchField;
import com.jmp.elastic.model.EventSearchRequest;
import com.jmp.elastic.model.IndexMapping;

public final class TestMockData {

    public static final String INDEX_NAME = "events";

    public static final String ID = "f1b7a2e8-f96b-4eb3-9007-c726b820b98d";

    public static final String TITLE = "From middle to senior";

    public static final String EVENT_DATE = "2023-01-22T16:00:00Z";

    public static final String PLACE = "EPAM k3";

    public static final String DESCRIPTION = "How to grow to senior";

    public static final List<String> SUB_TOPICS = List.of("teamwork", "hard-skills", "soft-skills");

    private TestMockData() {
    }

    public static EventRequestDto buildRequestDto() {
        return EventRequestDto.builder()
                .title(TITLE)
                .eventType(EventTypeDto.TECH_TALK)
                .eventDate(Instant.parse(EVENT_DATE))
                .place(PLACE)
                .description(DESCRIPTION)
                .subTopics(SUB_TOPICS)
                .build();
    }

    public static EventPatchRequestDto buildPatchRequestDto() {
        return EventPatchRequestDto.builder()
                .title(TITLE)
                .eventType(EventTypeDto.TECH_TALK)
                .eventDate(Instant.parse(EVENT_DATE))
                .place(PLACE)
                .description(DESCRIPTION)
                .subTopics(SUB_TOPICS)
                .build();
    }

    public static EventSearchRequestDto buildSearchRequestDto() {
        return EventSearchRequestDto.builder()
                .searchValuesByFields(Map.of(EventSearchFieldDto.TITLE, TITLE,
                        EventSearchFieldDto.EVENT_TYPE, EventTypeDto.TECH_TALK.getName(),
                        EventSearchFieldDto.EVENT_DATE, EVENT_DATE))
                .page(0)
                .build();
    }

    public static EventSearchRequest buildEventSearchRequest() {
        return EventSearchRequest.builder()
                .searchValuesByFields(Map.of(EventSearchField.TITLE, TITLE,
                        EventSearchField.EVENT_TYPE, EventTypeDto.TECH_TALK.getName(),
                        EventSearchField.EVENT_DATE, EVENT_DATE))
                .page(0)
                .build();
    }

    public static Event buildEvent(final Boolean isIdSetted) {
        final var event = Event.builder()
                .title(TITLE)
                .eventType(EventTypeDto.TECH_TALK.getName())
                .eventDate(Instant.parse(EVENT_DATE))
                .place(PLACE)
                .description(DESCRIPTION)
                .subTopics(SUB_TOPICS)
                .build();
        if (isIdSetted) {
            event.setId(ID);
        }
        return event;
    }

    public static EventDto buildEventDto() {
        return EventDto.builder()
                .id(ID)
                .title(TITLE)
                .eventType(EventTypeDto.TECH_TALK.getName())
                .eventDate(Instant.parse(EVENT_DATE))
                .place(PLACE)
                .description(DESCRIPTION)
                .subTopics(SUB_TOPICS)
                .build();
    }

    public static IndexRequestDto buildIndexRequestDto() {
        return IndexRequestDto.builder()
                .mappings(buildIndexMappingDto())
                .index(INDEX_NAME)
                .build();
    }

    public static IndexMappingDto buildIndexMappingDto() {
        return IndexMappingDto.builder()
                .properties(Map.of("title", new IndexMappingDto.FieldTypeDto("text")))
                .build();
    }

    public static IndexMapping buildIndexMapping() {
        return IndexMapping.builder()
                .properties(Map.of("title", new IndexMapping.FieldType("text")))
                .build();
    }

    public static ObjectMapper buildObjectMapper() {
        final var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(InstantSerializer.INSTANCE);
        final var objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new Jdk8Module())
                .registerModule(javaTimeModule).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @SneakyThrows
    public static String buildJson(final String path) {
        return Files.readString(new ClassPathResource(path).getFile().toPath());
    }

}
