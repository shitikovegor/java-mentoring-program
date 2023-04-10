package com.jmp.search;

import java.util.List;

import com.jmp.search.dto.BookDto;
import com.jmp.search.dto.SearchFieldNameDto;
import com.jmp.search.dto.SearchRequestDto;
import com.jmp.search.model.Book;
import com.jmp.search.model.SearchRequest;

public final class TestMockData {

    public static final String TITLE = "Test title";

    public static final String AUTHOR = "Surname, name";

    public static final String CONTENT = "Content";

    public static final String LANG = "en";

    public static final String TITLE_FIELD = "title";

    public static final String ID = "2a78c6dd-a2af-4e2e-aef8-53887c5d3d2e";

    public static SearchRequest buildSearchRequest(final boolean fulltext) {
        return SearchRequest.builder()
                .field(TITLE_FIELD)
                .value(TITLE)
                .facetField("authors")
                .fulltext(fulltext)
                .q("title:Test title")
                .build();
    }

    public static SearchRequestDto buildSearchRequestDto() {
        return SearchRequestDto.builder()
                .field(SearchFieldNameDto.TITLE)
                .value(TITLE)
                .facetField(SearchFieldNameDto.AUTHORS)
                .fulltext(true)
                .q("title:Test title")
                .build();
    }

    public static Book buildBook() {
        return buildBook(TITLE);
    }

    public static Book buildBook(final String title) {
        return Book.builder()
                .id(ID)
                .title(title)
                .authors(List.of(AUTHOR))
                .content(CONTENT)
                .language(LANG)
                .build();
    }

    public static BookDto buildBookDto() {
        return BookDto.builder()
                .id(ID)
                .title(TITLE)
                .authors(List.of(AUTHOR))
                .content(CONTENT)
                .language(LANG)
                .build();
    }

}
