package com.jmp.elastic.client.impl;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmp.elastic.TestMockData;
import com.jmp.elastic.configuration.properties.EventProperties;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.model.Event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticJavaApiClientTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ElasticsearchClient searchClient;

    private ElasticJavaApiClient client;

    @BeforeEach
    void setUp() {
        final var properties = new EventProperties();
        properties.setIndexName(TestMockData.INDEX_NAME);
        client = new ElasticJavaApiClient(searchClient, properties);
    }

    @SneakyThrows
    @Test
    void createEvent() {
        // given
        final var event = new Event();
        final var response = Mockito.mock(IndexResponse.class);
        doAnswer(invocation -> {
            final IndexRequest<Event> argument = invocation.getArgument(0);
            event.setId(argument.document().getId());
            return response;
        }).when(searchClient).index(any(IndexRequest.class));
        // when
        client.createEvent(event);
        // then
        assertThat(event.getId()).isNotBlank();
        Mockito.verify(searchClient).index(any(IndexRequest.class));
    }

    @SneakyThrows
    @Test
    void createEvent_SearchClientError_ThrowsException() {
        // given
        final var event = new Event();
        when(searchClient.index(any(IndexRequest.class))).thenThrow(new IOException());
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
        final var event = Event.builder()
                .id(TestMockData.ID)
                .build();
        final GetResponse<Event> response = GetResponse.of(b -> b
                .id(TestMockData.ID)
                .index(TestMockData.INDEX_NAME)
                .found(true)
                .source(event));
        when(searchClient.get(any(Function.class), eq(Event.class)))
                .thenReturn(response);
        // when
        final var result = client.getEvent(TestMockData.ID);
        // then
        assertThat(result).isPresent().get().isEqualTo(event);
    }

    @SneakyThrows
    @Test
    void getEvent_NotFound_Empty() {
        // given
        final GetResponse<Event> response = GetResponse.of(b -> b
                .id(TestMockData.ID)
                .index(TestMockData.INDEX_NAME)
                .found(false));
        when(searchClient.get(any(Function.class), eq(Event.class)))
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
        final var response = Mockito.mock(UpdateResponse.class);
        when(searchClient.update(any(Function.class), eq(Event.class))).thenReturn(response);
        // when
        client.updateEvent(TestMockData.ID, event);
        // then
        Mockito.verify(searchClient).update(any(Function.class), eq(Event.class));
    }

    @SneakyThrows
    @Test
    void existsEvent() {
        // given
        final var response = Mockito.mock(BooleanResponse.class);
        when(response.value()).thenReturn(Boolean.TRUE);
        when(searchClient.exists(any(Function.class))).thenReturn(response);
        // when
        final var result = client.existsEvent(TestMockData.ID);
        // then
        Mockito.verify(searchClient).exists(any(Function.class));
        assertThat(result).isTrue();
    }

    @SneakyThrows
    @Test
    void deleteEvent() {
        // given
        final var response = Mockito.mock(DeleteResponse.class);
        when(searchClient.delete(any(Function.class))).thenReturn(response);
        // when
        client.deleteEvent(TestMockData.ID);
        // then
        Mockito.verify(searchClient).delete(any(Function.class));
    }

    @SneakyThrows
    @Test
    void searchEvents() {
        // given
        final var event = TestMockData.buildEvent(true);
        final var searchRequest = TestMockData.buildEventSearchRequest();
        final SearchResponse<Event> response = SearchResponse
                .of(r -> r
                        .hits(h -> h.hits(hit -> hit.id(TestMockData.ID).index(TestMockData.INDEX_NAME).source(event)))
                        .took(1L)
                        .timedOut(false)
                        .shards(s -> s.failed(0)
                                .successful(1)
                                .total(1)));
        when(searchClient.search(any(Function.class), eq(Event.class)))
                .thenReturn(response);
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
        final var response = Mockito.mock(DeleteByQueryResponse.class);
        when(searchClient.deleteByQuery(any(Function.class))).thenReturn(response);
        // when
        client.deleteEventsByTitle("title");
        // then
        verify(searchClient).deleteByQuery(any(Function.class));
    }

    @SneakyThrows
    @Test
    void createIndex() {
        // given
        final var indicesClient = Mockito.mock(ElasticsearchIndicesClient.class);
        final var response = Mockito.mock(CreateIndexResponse.class);
        when(searchClient.indices()).thenReturn(indicesClient);
        when(indicesClient.create(any(Function.class))).thenReturn(response);
        // when
        client.createIndex(TestMockData.INDEX_NAME);
        // then
        verify(indicesClient).create(any(Function.class));
        verify(searchClient).indices();
    }

    @SneakyThrows
    @Test
    void updateMapping() {
        // given
        final var indicesClient = Mockito.mock(ElasticsearchIndicesClient.class);
        final var response = Mockito.mock(PutMappingResponse.class);
        when(searchClient.indices()).thenReturn(indicesClient);
        when(indicesClient.putMapping(any(PutMappingRequest.class))).thenReturn(response);
        // when
        client.updateMapping(TestMockData.INDEX_NAME, "{}");
        // then
        verify(indicesClient).putMapping(any(PutMappingRequest.class));
        verify(searchClient).indices();
    }

    @SneakyThrows
    @Test
    void getIndices() {
        // given
        final var indicesClient = Mockito.mock(ElasticsearchIndicesClient.class);
        final var record = IndexMappingRecord.of(
                r -> r.mappings(m -> m.properties(Map.of("title", Property.of(p -> p.text(t -> t))))));
        final var response = GetMappingResponse.of(r -> r.result(Map.of("events", record)));
        when(searchClient.indices()).thenReturn(indicesClient);
        when(indicesClient.getMapping()).thenReturn(response);
        // when
        final var indices = client.getIndices();
        // then
        verify(indicesClient).getMapping();
        verify(searchClient).indices();
        assertThat(indices).isNotEmpty()
                .hasSize(1)
                .containsEntry("events", TestMockData.buildIndexMapping());
    }

}
