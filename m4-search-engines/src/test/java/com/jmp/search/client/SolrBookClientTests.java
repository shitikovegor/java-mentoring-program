package com.jmp.search.client;

import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.client.solrj.response.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.data.solr.server.SolrClientFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import com.jmp.search.TestMockData;
import com.jmp.search.configuration.properties.BookProperties;
import com.jmp.search.exception.BookException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolrBookClientTests {

    private static final String COLLECTION_NAME = "books";

    public static final String SUGGESTER_NAME = "suggester";

    private SolrBookClient client;

    @Mock
    private SolrTemplate template;

    @BeforeEach
    void setUp() {
        final var properties = new BookProperties();
        properties.setCollection(COLLECTION_NAME);
        properties.setPageLimit(10);
        properties.setSuggesterName(SUGGESTER_NAME);
        client = new SolrBookClient(properties, template);
    }

    @Test
    void searchBooks_Filter() {
        // given
        final var searchRequest = TestMockData.buildSearchRequest(false);
        final var book = TestMockData.buildBook();
        final FacetPage facetPage = new SolrResultPage<>(List.of(book));
        when(template.queryForFacetPage(eq(COLLECTION_NAME), any(), any())).thenReturn(facetPage);
        // when
        final var result = client.searchBooks(searchRequest);
        // then
        assertThat(result).isEqualTo(facetPage);
    }

    @Test
    void searchBooks_FullText() {
        // given
        final var searchRequest = TestMockData.buildSearchRequest(true);
        final var book = TestMockData.buildBook();
        final FacetPage facetPage = new SolrResultPage<>(List.of(book));
        when(template.queryForFacetPage(eq(COLLECTION_NAME), any(), any())).thenReturn(facetPage);
        // when
        final var result = client.searchBooks(searchRequest);
        // then
        assertThat(result).isEqualTo(facetPage);
    }

    @SneakyThrows
    @Test
    void getSuggestions() {
        // given
        final var suggestion = new Suggestion("Test", 0, null);
        final var solrClient = Mockito.mock(SolrClient.class);
        final var solrClientFactory = Mockito.mock(SolrClientFactory.class);
        final var queryResponse = Mockito.mock(QueryResponse.class);
        final var suggesterResponse = Mockito.mock(SuggesterResponse.class);
        ReflectionTestUtils.setField(template, "solrClientFactory", solrClientFactory);
        when(solrClientFactory.getSolrClient()).thenReturn(solrClient);
        when(solrClient.query(eq(COLLECTION_NAME), any())).thenReturn(queryResponse);
        when(queryResponse.getSuggesterResponse()).thenReturn(suggesterResponse);
        when(suggesterResponse.getSuggestions()).thenReturn(Map.of(SUGGESTER_NAME, List.of(suggestion)));
        // when
        final var suggestions = client.getSuggestions("t");
        // then
        assertThat(suggestions).isNotEmpty().hasSize(1)
                .first().satisfies(element -> assertThat(element).isEqualTo("Test"));
    }

    @SneakyThrows
    @Test
    void getSuggestions_ClientThrowsException() {
        // given
        final var solrClient = Mockito.mock(SolrClient.class);
        final var solrClientFactory = Mockito.mock(SolrClientFactory.class);
        ReflectionTestUtils.setField(template, "solrClientFactory", solrClientFactory);
        when(solrClientFactory.getSolrClient()).thenReturn(solrClient);
        when(solrClient.query(eq(COLLECTION_NAME), any())).thenThrow(
                new BookException("message", "code", HttpStatus.BAD_REQUEST));
        // when
        assertThatThrownBy(() -> client.getSuggestions("t"))
                // then
                .isInstanceOf(BookException.class)
                .hasMessage("Suggestion request error: message");
    }

}
