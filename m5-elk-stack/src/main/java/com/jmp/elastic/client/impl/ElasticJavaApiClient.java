package com.jmp.elastic.client.impl;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jmp.elastic.client.EventClient;
import com.jmp.elastic.client.IndexClient;
import com.jmp.elastic.configuration.properties.EventProperties;
import com.jmp.elastic.exception.ErrorCode;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.exception.IndexException;
import com.jmp.elastic.model.Event;
import com.jmp.elastic.model.EventSearchField;
import com.jmp.elastic.model.EventSearchRequest;
import com.jmp.elastic.model.IndexMapping;

@Slf4j
@Profile("!rest")
@Component
@RequiredArgsConstructor
public class ElasticJavaApiClient implements EventClient, IndexClient {

    private static final String ERROR_MESSAGE = "Error on elastic client side";

    private final ElasticsearchClient client;

    private final EventProperties properties;

    @Override
    public void createEvent(final Event event) {
        try {
            final var eventId = UUID.randomUUID().toString();
            event.setId(eventId);
            final var request = new IndexRequest.Builder<Event>()
                    .index(properties.getIndexName())
                    .id(eventId)
                    .document(event)
                    .build();
            final var response = client.index(request);
            log.info("Event {} is indexed with version {}", event, response.version());
        } catch (final IOException e) {
            log.error("Error in time of creation event with title {}", event.getTitle(), e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public Optional<Event> getEvent(final String id) {
        try {
            final var response = client.get(g -> g.index(properties.getIndexName()).id(id),
                    Event.class);
            return response.found() ? Optional.of(response.source()) : Optional.empty();
        } catch (final IOException e) {
            log.error("Error in time of retrieving event with id {}", id, e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void updateEvent(final String id, final Event eventToUpdate) {
        try {
            client.update(g -> g.index(properties.getIndexName())
                    .id(id)
                    .doc(eventToUpdate),
                    Event.class);
            log.info("Event {} is updated", eventToUpdate);
        } catch (final IOException e) {
            log.error("Error in time of updating event with id {}", eventToUpdate.getId(), e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public boolean existsEvent(final String id) {
        try {
            final var response = client.exists(g -> g.index(properties.getIndexName()).id(id));
            return response.value();
        } catch (final IOException e) {
            log.error("Error in time of check existing of event with id {}", id, e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void deleteEvent(final String id) {
        try {
            final var response = client.delete(g -> g.index(properties.getIndexName()).id(id));
            log.info("Event with id {} is deleted with version {}", id, response.version());
        } catch (final IOException e) {
            log.error("Error in time of deleting event with id {}", id, e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public List<Event> searchEvents(final EventSearchRequest searchRequest) {
        try {
            final var searchValues = searchRequest.getSearchValuesByFields();
            final List<Query> queriesToSearch = (searchValues == null || searchValues.isEmpty())
                    ? Collections.emptyList() : buildSearchQuery(searchValues);
            final var boolQuery = new BoolQuery.Builder().must(queriesToSearch).build();
            final var response = client.search(s -> s.index(properties.getIndexName())
                    .query(q -> q.bool(boolQuery)),
                    Event.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("Error in time of searching events", e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void deleteEventsByTitle(final String title) {
        try {
            final var query = buildMatchQuery(EventSearchField.TITLE, title);
            client.deleteByQuery(r -> r.index(properties.getIndexName())
                    .query(query));
        } catch (final IOException e) {
            log.error("Error in time of deleting events by title {}", title, e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    private List<Query> buildSearchQuery(final Map<EventSearchField, String> searchValues) {
        return searchValues.entrySet().stream()
                .map(entry -> switch (entry.getKey()) {
                case TITLE, EVENT_TYPE -> buildMatchQuery(entry.getKey(), entry.getValue());
                case EVENT_DATE -> buildRangeQuery(entry.getKey(), entry.getValue());
                })
                .collect(Collectors.toList());
    }

    private Query buildRangeQuery(final EventSearchField field, final String value) {
        return RangeQuery.of(r -> r.field(field.getName())
                .from(value)
                .to(Instant.now().toString()))
                ._toQuery();
    }

    private Query buildMatchQuery(final EventSearchField field, final String value) {
        return MatchQuery.of(m -> m.field(field.getName())
                .query(value))
                ._toQuery();
    }

    @Override
    public void createIndex(final String indexName) {
        try {
            client.indices().create(c -> c.index(indexName));
        } catch (final IOException e) {
            log.error("Error in time of creating index {}", indexName, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void updateMapping(final String index, final String mappingJson) {
        try {
            final var request = new PutMappingRequest.Builder().index(index)
                    .withJson(new StringReader(mappingJson))
                    .build();
            client.indices().putMapping(request);
        } catch (final IOException e) {
            log.error("Error in time of adding mapping to index {}", index, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public Map<String, IndexMapping> getIndices() {
        try {
            return client.indices().getMapping().result().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> buildMappingDto(e.getValue().mappings())));
        } catch (final IOException e) {
            log.error("Error in time of getting indices", e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR,
                    HttpStatus.BAD_GATEWAY, e);
        }
    }

    private IndexMapping buildMappingDto(final TypeMapping mappings) {
        final var properties = mappings.properties();
        final var mappedProperties = properties.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> new IndexMapping.FieldType(e.getValue()._kind().jsonValue())));
        return IndexMapping.builder()
                .properties(mappedProperties).build();
    }

}
