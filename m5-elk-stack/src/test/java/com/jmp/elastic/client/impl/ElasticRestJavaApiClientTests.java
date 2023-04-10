package com.jmp.elastic.client.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeaderElement;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import com.jmp.elastic.TestMockData;
import com.jmp.elastic.configuration.properties.EventProperties;
import com.jmp.elastic.configuration.properties.RestClientProperties;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.model.Event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticRestJavaApiClientTests {

    @Mock
    private RestClient searchClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Response response;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpEntity httpEntity;

    private final ObjectMapper mapper = TestMockData.buildObjectMapper();

    private ElasticRestJavaApiClient client;

    @BeforeEach
    void setUp() {
        final var eventProperties = new EventProperties();
        eventProperties.setIndexName(TestMockData.INDEX_NAME);
        final var searchClientProperties = new RestClientProperties();
        searchClientProperties.setCountKey("count");
        searchClientProperties.setSourceKey("_source");
        searchClientProperties.setHitsKey("hits");
        client = new ElasticRestJavaApiClient(searchClient, eventProperties, searchClientProperties, mapper);
    }

    @SneakyThrows
    @Test
    void createEvent() {
        // given
        final var event = new Event();
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.createEvent(event);
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void createEvent_SearchClientError_ThrowsException() {
        // given
        final var event = new Event();
        when(searchClient.performRequest(any())).thenThrow(new IOException());
        // when
        assertThatThrownBy(() -> client.createEvent(event))
                // then
                .isInstanceOf(EventException.class)
                .hasMessageContaining("Error");
    }

    @SneakyThrows
    @Test
    void getEvent() {
        // given
        final var event = TestMockData.buildEvent(true);
        mockResponse(mapper.writeValueAsString(Map.of("_source", event)));
        // when
        final var result = client.getEvent(TestMockData.ID);
        // then
        assertThat(result).isPresent().get().isEqualTo(event);
    }

    @SneakyThrows
    @Test
    void getEvent_NotFound_Empty() {
        // given
        when(response.getStatusLine().getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
        when(searchClient.performRequest(any()))
                .thenReturn(response);
        // when
        final var result = client.getEvent(TestMockData.ID);
        // then
        assertThat(result).isEmpty();
    }

    @SneakyThrows
    @Test
    void updateEvent() {
        // given
        final var event = new Event();
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.updateEvent(TestMockData.ID, event);
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void existsEvent() {
        // given
        final var eventString = Files.readString(new ClassPathResource("mock/count-response.json").getFile().toPath());
        mockResponse(eventString);
        // when
        final var result = client.existsEvent(TestMockData.ID);
        // then
        assertThat(result).isTrue();
    }

    @SneakyThrows
    @Test
    void deleteEvent() {
        // given
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.deleteEvent(TestMockData.ID);
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void searchEvents() {
        // given
        final var searchRequest = TestMockData.buildEventSearchRequest();
        final var event = mapper.readValue(
                Files.readString(new ClassPathResource("mock/event.json").getFile().toPath()), Event.class);
        mockResponse(Files.readString(new ClassPathResource("mock/search-response.json").getFile().toPath()));
        // when
        final var events = client.searchEvents(searchRequest);
        // then
        assertThat(events).isNotEmpty().hasSize(1)
                .first().satisfies(element -> assertThat(element).isEqualTo(event));
    }

    @SneakyThrows
    @Test
    void deleteEventsByTitle() {
        // given
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.deleteEventsByTitle("From");
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void createIndex() {
        // given
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.createIndex(TestMockData.INDEX_NAME);
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void updateMapping() {
        // given
        when(searchClient.performRequest(any())).thenReturn(response);
        // when
        client.updateMapping(TestMockData.INDEX_NAME, "{}");
        // then
        Mockito.verify(searchClient).performRequest(any());
    }

    @SneakyThrows
    @Test
    void getIndices() {
        // given
        mockResponse(Files.readString(new ClassPathResource("mock/indices.json").getFile().toPath()));
        // when
        final var indices = client.getIndices();
        // then
        Mockito.verify(searchClient).performRequest(any());
        assertThat(indices).isNotEmpty()
                .hasSize(1)
                .containsEntry("events", TestMockData.buildIndexMapping());
    }

    @SneakyThrows
    private void mockResponse(final String content) {
        final var headerElement = new BasicHeaderElement("application/json", null);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
        when(httpEntity.getContentType().getElements()).thenReturn(new HeaderElement[] { headerElement });
        when(response.getEntity()).thenReturn(httpEntity);
        when(searchClient.performRequest(any())).thenReturn(response);
    }

}
