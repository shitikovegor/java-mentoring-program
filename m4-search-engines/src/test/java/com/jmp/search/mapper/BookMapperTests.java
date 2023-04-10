package com.jmp.search.mapper;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.solr.core.query.result.SimpleFacetFieldEntry;

import com.jmp.search.TestMockData;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookMapperTests {

    private BookMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(BookMapper.class);
    }

    @Test
    void toBookDto() {
        // given
        final var book = TestMockData.buildBook();
        // when
        final var result = mapper.toBookDto(book);
        // then
        assertThat(result.getTitle()).isEqualTo(TestMockData.TITLE);
        assertThat(result.getAuthors()).isEqualTo(List.of(TestMockData.AUTHOR));
        assertThat(result.getContent()).isEqualTo(TestMockData.CONTENT);
        assertThat(result.getLanguage()).isEqualTo(TestMockData.LANG);
    }

    @Test
    void toBookDtos() {
        // given
        final var books = List.of(TestMockData.buildBook());
        // when
        final var result = mapper.toBookDtos(books);
        // then
        assertThat(result).isNotEmpty().hasSize(1).first().satisfies(book -> {
            assertThat(book.getTitle()).isEqualTo(TestMockData.TITLE);
            assertThat(book.getAuthors()).isEqualTo(List.of(TestMockData.AUTHOR));
            assertThat(book.getContent()).isEqualTo(TestMockData.CONTENT);
            assertThat(book.getLanguage()).isEqualTo(TestMockData.LANG);
        });
    }

    @Test
    void toBook() {
        // given
        final var bookDto = TestMockData.buildBookDto();
        // when
        final var result = mapper.toBook(bookDto);
        // then
        assertThat(result.getTitle()).isEqualTo(TestMockData.TITLE);
        assertThat(result.getAuthors()).isEqualTo(List.of(TestMockData.AUTHOR));
        assertThat(result.getContent()).isEqualTo(TestMockData.CONTENT);
        assertThat(result.getLanguage()).isEqualTo(TestMockData.LANG);
    }

    @Test
    void toSearchRequest() {
        // given
        final var searchRequestDto = TestMockData.buildSearchRequestDto();
        // when
        final var result = mapper.toSearchRequest(searchRequestDto);
        // then
        assertThat(result.getField()).isEqualTo(TestMockData.TITLE_FIELD);
        assertThat(result.getValue()).isEqualTo(TestMockData.TITLE);
        assertThat(result.getFacetField()).isEqualTo("authors");
        assertThat(result.isFulltext()).isTrue();
        assertThat(result.getQ()).isEqualTo("title:Test title");
    }

    @Test
    void toFacetDto() {
        // given
        final var fieldEntry = new SimpleFacetFieldEntry(null, TestMockData.TITLE_FIELD, 3);
        // when
        final var result = mapper.toFacetDto(fieldEntry);
        // then
        assertThat(result.getValue()).isEqualTo(TestMockData.TITLE_FIELD);
        assertThat(result.getValueCount()).isEqualTo(3);
    }

    @Test
    void toFacetDtos() {
        // given
        final var fieldEntry = new SimpleFacetFieldEntry(null, TestMockData.TITLE_FIELD, 3);
        // when
        final var result = mapper.toFacetDtos(List.of(fieldEntry));
        // then
        assertThat(result).isNotEmpty().hasSize(1).first().satisfies(facet -> {
            assertThat(facet.getValue()).isEqualTo(TestMockData.TITLE_FIELD);
            assertThat(facet.getValueCount()).isEqualTo(3);
        });
    }

}
