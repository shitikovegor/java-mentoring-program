package com.jmp.search.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.result.SimpleFacetFieldEntry;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import com.jmp.search.TestMockData;
import com.jmp.search.client.LoadBookClient;
import com.jmp.search.client.SolrBookClient;
import com.jmp.search.dto.FacetDto;
import com.jmp.search.exception.BookException;
import com.jmp.search.mapper.BookMapper;
import com.jmp.search.model.Book;
import com.jmp.search.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    private LoadBookClient loadClient;

    @Mock
    private BookRepository repository;

    @Mock
    private BookMapper mapper;

    @Mock
    private SolrBookClient solrClient;

    private BookService service;

    @BeforeEach
    void setUp() {
        service = new BookService(loadClient, repository, mapper, solrClient);
    }

    @Test
    void indexBooks_EmptySolrRepository_SaveAll() {
        // given
        final var books = List.of(TestMockData.buildBook());
        when(loadClient.getBooks()).thenReturn(books);
        when(repository.count()).thenReturn(0L);
        doAnswer(invocation -> invocation.<Book>getArgument(0))
                .when(repository).saveAll(books);
        // when
        service.indexBooks();
        // then
        Mockito.verify(repository).saveAll(books);
    }

    @Test
    void indexBooks_RepositoryContainsData_OneBookSaved() {
        // given
        final var book1 = TestMockData.buildBook("title1");
        final var book2 = TestMockData.buildBook("title2");
        final var books = List.of(book1, book2);
        when(loadClient.getBooks()).thenReturn(books);
        when(repository.count()).thenReturn(1L);
        when(repository.findByTitle("title1")).thenReturn(book1);
        doAnswer(invocation -> invocation.<Book>getArgument(0))
                .when(repository).saveAll(List.of(book2));
        // when
        service.indexBooks();
        // then
        Mockito.verify(repository).saveAll(List.of(book2));
    }

    @Test
    void getBookById() {
        // given
        final var book = TestMockData.buildBook();
        final var bookDto = TestMockData.buildBookDto();
        when(repository.findById(any())).thenReturn(Optional.of(book));
        when(mapper.toBookDto(book)).thenReturn(bookDto);
        // when
        final var result = service.getBookById("id");
        // then
        assertThat(result.getTitle()).isEqualTo(TestMockData.TITLE);
        assertThat(result.getAuthors()).isEqualTo(List.of(TestMockData.AUTHOR));
        assertThat(result.getLanguage()).isEqualTo(TestMockData.LANG);
        assertThat(result.getContent()).isEqualTo(TestMockData.CONTENT);
    }

    @Test
    void getBookById_NotFound() {
        // given
        when(repository.findById(any())).thenReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> service.getBookById("id"))
                // then
                .isInstanceOf(BookException.class)
                .hasMessageContaining(" doesn't exist");
    }

    @Test
    void searchBooks() {
        // given
        final var book = TestMockData.buildBook();
        final var bookDto = TestMockData.buildBookDto();
        final var searchRequest = TestMockData.buildSearchRequest(true);
        final var searchRequestDto = TestMockData.buildSearchRequestDto();
        final var facetDto = new FacetDto(TestMockData.TITLE_FIELD, 3);
        final var solrResultPage = new SolrResultPage<>(List.of(book));
        final var fieldEntry = new SimpleFacetFieldEntry(new SimpleField(TestMockData.TITLE_FIELD),
                TestMockData.TITLE, 1);
        solrResultPage.addFacetResultPage(new SolrResultPage<>(List.of(fieldEntry)),
                new SimpleField(TestMockData.TITLE_FIELD));
        when(solrClient.searchBooks(searchRequest)).thenReturn(solrResultPage);
        when(mapper.toSearchRequest(searchRequestDto)).thenReturn(searchRequest);
        when(mapper.toBookDtos(List.of(book))).thenReturn(List.of(bookDto));
        when(mapper.toFacetDtos(any())).thenReturn(List.of(facetDto));
        // when
        final var result = service.searchBooks(searchRequestDto);
        // then
        assertThat(result.getNumFound()).isEqualTo(1);
        assertThat(result.getBooks()).isEqualTo(List.of(bookDto));
        assertThat(result.getFacetsByField()).isEqualTo(Map.of(TestMockData.TITLE_FIELD, List.of(facetDto)));
    }

    @Test
    void getSuggestions() {
        // given
        final var suggestions = List.of("quality");
        when(solrClient.getSuggestions("q")).thenReturn(suggestions);
        // when
        final var result = service.getSuggestions("q");
        // then
        assertThat(result).isNotEmpty().isEqualTo(suggestions);
    }

}
