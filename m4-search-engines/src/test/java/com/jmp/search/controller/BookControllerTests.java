package com.jmp.search.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;
import org.apache.solr.client.solrj.response.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.result.SimpleFacetFieldEntry;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jmp.search.TestMockData;
import com.jmp.search.model.Book;
import com.jmp.search.repository.BookRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTests {

    @MockBean
    private BookRepository repository;

    @SpyBean
    private SolrTemplate solrTemplate;

    @MockBean
    private SolrClient solrClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

    }

    @SneakyThrows
    @Test
    void indexBooks() {
        // given
        final var books = List.of(TestMockData.buildBook());
        when(repository.count()).thenReturn(0L);
        doAnswer(invocation -> invocation.<Book>getArgument(0))
                .when(repository).saveAll(books);
        // when
        mockMvc.perform(post("/api/v1/books/index")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @SneakyThrows
    @Test
    void search() {
        // given
        final var searchRequestDto = TestMockData.buildSearchRequestDto();
        final var requestJson = mapper.writeValueAsString(searchRequestDto);
        final var book = TestMockData.buildBook();
        final var solrResultPage = new SolrResultPage<>(List.of(book));
        final var fieldEntry = new SimpleFacetFieldEntry(new SimpleField(TestMockData.TITLE_FIELD),
                TestMockData.TITLE, 1);
        solrResultPage.addFacetResultPage(new SolrResultPage<>(List.of(fieldEntry)),
                new SimpleField(TestMockData.TITLE_FIELD));
        doReturn(solrResultPage).when(solrTemplate).queryForFacetPage(eq("books"), any(), eq(Book.class));
        // when
        mockMvc.perform(post("/api/v1/books/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                // then
                .andExpect(status().isOk())
                .andExpect(content().json(buildJson("expected/search-result.json")));
    }

    @SneakyThrows
    @Test
    void getBook() {
        // given
        final var book = TestMockData.buildBook();
        when(repository.findById(TestMockData.ID)).thenReturn(Optional.of(book));
        // when
        mockMvc.perform(get("/api/v1/books/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().json(buildJson("expected/book.json")));
    }

    @SneakyThrows
    @Test
    void getBook_NotFound() {
        // given
        when(repository.findById(TestMockData.ID)).thenReturn(Optional.empty());
        // when
        mockMvc.perform(get("/api/v1/books/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getSuggestions() {
        // given
        final var suggestion1 = new Suggestion("Test", 0, null);
        final var suggestion2 = new Suggestion("title", 0, null);
        final var queryResponse = Mockito.mock(QueryResponse.class);
        final var suggesterResponse = Mockito.mock(SuggesterResponse.class);
        when(solrClient.query(eq("books"), any())).thenReturn(queryResponse);
        when(queryResponse.getSuggesterResponse()).thenReturn(suggesterResponse);
        when(suggesterResponse.getSuggestions()).thenReturn(Map.of("bookSuggester", List.of(suggestion1, suggestion2)));

        // when
        mockMvc.perform(get("/api/v1/books/suggest?query=t")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(content().json(buildJson("expected/suggestion.json")));
    }

    @SneakyThrows
    @Test
    void getSuggestions_Error() {
        // given
        when(solrClient.query(eq("books"), any())).thenThrow(new IOException());
        // when
        mockMvc.perform(get("/api/v1/books/suggest?query=t")
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isBadGateway());
    }

    @SneakyThrows
    private String buildJson(final String resourceName) {
        return IOUtils
                .toString(Objects.requireNonNull(
                        this.getClass().getClassLoader()
                                .getResourceAsStream(resourceName)),
                        StandardCharsets.UTF_8);
    }

}
