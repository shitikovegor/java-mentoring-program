package com.jmp.elastic.client.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jmp.elastic.client.EventClient;
import com.jmp.elastic.client.IndexClient;
import com.jmp.elastic.configuration.properties.EventProperties;
import com.jmp.elastic.configuration.properties.RestClientProperties;
import com.jmp.elastic.exception.ErrorCode;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.exception.IndexException;
import com.jmp.elastic.model.Event;
import com.jmp.elastic.model.EventSearchField;
import com.jmp.elastic.model.EventSearchRequest;
import com.jmp.elastic.model.IndexMapping;
import com.jmp.elastic.model.rest.EventDocument;
import com.jmp.elastic.model.rest.IndexData;
import com.jmp.elastic.util.RestClientRequestPath;
import com.jmp.elastic.util.StringQueryBuilder;

@Slf4j
@Profile("rest")
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(RestClientProperties.class)
public class ElasticRestJavaApiClient implements EventClient, IndexClient {

    private static final String ERROR_MESSAGE = "Error on elastic client side";

    private static final String ID_FIELD = "id";

    private final RestClient client;

    private final EventProperties properties;

    private final RestClientProperties clientProperties;

    private final ObjectMapper objectMapper;

    @Override
    public void createEvent(final Event event) {
        try {
            final var eventId = UUID.randomUUID().toString();
            event.setId(eventId);
            final var request = new Request(RequestMethod.POST.name(),
                    buildEndpoint(RestClientRequestPath.CREATE, eventId));
            request.setEntity(buildEntity(objectMapper.writeValueAsString(event)));
            client.performRequest(request);
            log.info("Event {} is indexed", event);
        } catch (final IOException e) {
            log.error("Error in time of creation event with title {}", event.getTitle(), e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public Optional<Event> getEvent(final String id) {
        try {
            final var request = new Request(RequestMethod.GET.name(),
                    buildEndpoint(RestClientRequestPath.GET, id));
            request.addParameter("ignore", String.valueOf(HttpStatus.NOT_FOUND.value()));
            final var response = client.performRequest(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            final var source = objectMapper.readValue(getJsonNode(response, clientProperties.getSourceKey()).traverse(),
                    Event.class);
            return Optional.of(source);
        } catch (final IOException e) {
            log.error("Error in time of retrieving event with id {}", id, e);
            throw new EventException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void updateEvent(final String id, final Event eventToUpdate) {
        try {
            final var request = new Request(RequestMethod.POST.name(),
                    buildEndpoint(RestClientRequestPath.UPDATE, id));
            final var stringEvent = objectMapper.writeValueAsString(eventToUpdate);
            final var updateQuery = StringQueryBuilder.buildUpdateQuery(stringEvent);
            request.setEntity(buildEntity(updateQuery));
            client.performRequest(request);
            log.info("Event {} is updated", eventToUpdate);
        } catch (final IOException e) {
            log.error("Error in time of updating event with id {}", eventToUpdate.getId(), e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public boolean existsEvent(final String id) {
        try {
            final var request = new Request(RequestMethod.GET.name(),
                    buildEndpoint(RestClientRequestPath.COUNT));
            request.setEntity(buildEntity(StringQueryBuilder.buildQueryWithMatch(ID_FIELD, id)));
            final var response = client.performRequest(request);
            return objectMapper.readValue(getJsonNode(response, clientProperties.getCountKey()).traverse(),
                    Boolean.class);
        } catch (final IOException e) {
            log.error("Search client side error: ", e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void deleteEvent(final String id) {
        try {
            final var request = new Request(RequestMethod.DELETE.name(),
                    buildEndpoint(RestClientRequestPath.GET, id));
            client.performRequest(request);
            log.info("Event with id {} is deleted", id);
        } catch (final IOException e) {
            log.error("Error in time of deleting event with id {}", id, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public List<Event> searchEvents(final EventSearchRequest searchRequest) {
        try {
            final var request = new Request(RequestMethod.GET.name(),
                    buildEndpoint(RestClientRequestPath.SEARCH));
            final var searchValues = searchRequest.getSearchValuesByFields();
            if (searchValues != null && !searchValues.isEmpty()) {
                final var query = buildSearchQuery(searchValues);
                request.setEntity(buildEntity(query));
            }
            final var response = client.performRequest(request);
            final var hits = getJsonNode(response, clientProperties.getHitsKey()).get(clientProperties.getHitsKey());
            final List<EventDocument> foundEvents = objectMapper.readValue(hits.traverse(), new TypeReference<>() {

            });
            return foundEvents.stream()
                    .map(EventDocument::getSource)
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            log.error("Search client side error: ", e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void deleteEventsByTitle(final String title) {
        try {
            final var query = StringQueryBuilder.buildQueryWithMatch(EventSearchField.TITLE.getName(), title);
            final var request = new Request(RequestMethod.POST.name(),
                    buildEndpoint(RestClientRequestPath.DELETE_BY_QUERY));
            request.setEntity(buildEntity(query));
            client.performRequest(request);
        } catch (final IOException e) {
            log.error("Search client side error: ", e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    private String buildEndpoint(final String... pathElements) {
        final var indexPath = RestClientRequestPath.SEPARATOR + properties.getIndexName();
        return Arrays.stream(pathElements)
                .reduce(indexPath, String::concat);
    }

    private StringEntity buildEntity(final String entityAsString) {
        return new StringEntity(entityAsString, ContentType.APPLICATION_JSON);
    }

    private JsonNode getJsonNode(final Response response, final String fieldToGet) throws IOException {
        final var stringEntity = EntityUtils.toString(response.getEntity());
        final var jsonObject = objectMapper.readTree(stringEntity);
        return jsonObject.get(fieldToGet);
    }

    private String buildSearchQuery(final Map<EventSearchField, String> searchValuesByFields) {
        final var searchQueries = searchValuesByFields.entrySet().stream()
                .map(entry -> switch (entry.getKey()) {
                case TITLE, EVENT_TYPE -> StringQueryBuilder.buildMatchQuery(entry.getKey().getName(),
                        entry.getValue());
                case EVENT_DATE -> StringQueryBuilder.buildRangeQuery(entry.getKey().getName(), entry.getValue());
                })
                .collect(Collectors.toList());
        final var booleanQuery = StringQueryBuilder.buildBooleanQuery(searchQueries);
        return StringQueryBuilder.buildQuery(booleanQuery);
    }

    @Override
    public void createIndex(final String index) {
        try {
            client.performRequest(new Request(RequestMethod.PUT.name(), RestClientRequestPath.SEPARATOR + index));
        } catch (final IOException e) {
            log.error("Error in time of creating index {}", index, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public void updateMapping(final String index, final String mappingJson) {
        try {
            final var entity = new StringEntity(mappingJson, ContentType.APPLICATION_JSON);
            final var request = new Request(RequestMethod.PUT.name(),
                    RestClientRequestPath.SEPARATOR + index + RestClientRequestPath.MAPPING);
            request.setEntity(entity);
            client.performRequest(request);
        } catch (final IOException e) {
            log.error(ERROR_MESSAGE, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

    @Override
    public Map<String, IndexMapping> getIndices() {
        try {
            final var response = client.performRequest(
                    new Request(RequestMethod.GET.name(), RestClientRequestPath.MAPPING));
            final var stringEntity = EntityUtils.toString(response.getEntity());
            final Map<String, IndexData> indices = objectMapper.readValue(stringEntity, new TypeReference<>() {

            });
            return indices.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getMappings()));
        } catch (final IOException e) {
            log.error(ERROR_MESSAGE, e);
            throw new IndexException(ERROR_MESSAGE, ErrorCode.SEARCH_CLIENT_ERROR, HttpStatus.BAD_GATEWAY, e);
        }
    }

}
