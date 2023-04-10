package com.jmp.search.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jmp.search.client.LoadBookClient;
import com.jmp.search.client.SolrBookClient;
import com.jmp.search.dto.BookDto;
import com.jmp.search.dto.SearchRequestDto;
import com.jmp.search.dto.SearchResultDto;
import com.jmp.search.exception.BookException;
import com.jmp.search.exception.ErrorCode;
import com.jmp.search.mapper.BookMapper;
import com.jmp.search.repository.BookRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final LoadBookClient loadClient;

    private final BookRepository repository;

    private final BookMapper mapper;

    private final SolrBookClient solrClient;

    public void indexBooks() {
        final var books = loadClient.getBooks();
        final var booksToSave = repository.count() == 0 ? books : books.stream()
                .filter(book -> repository.findByTitle(book.getTitle()) == null)
                .collect(Collectors.toList());
        repository.saveAll(booksToSave);
        log.info("Books are saved");
    }

    public BookDto getBookById(final String id) {
        return repository.findById(id)
                .map(mapper::toBookDto)
                .orElseThrow(
                        () -> new BookException("Book with id " + id + " doesn't exist", ErrorCode.BOOK_NOT_FOUND,
                                HttpStatus.NOT_FOUND));
    }

    public SearchResultDto searchBooks(final SearchRequestDto searchRequest) {
        final var searchResult = solrClient.searchBooks(mapper.toSearchRequest(searchRequest));
        final var books = mapper.toBookDtos(searchResult.getContent());
        final var facetsByField = searchResult.getFacetFields().stream().findFirst()
                .map(key -> Map.of(key.getName(),
                        mapper.toFacetDtos(searchResult.getFacetResultPage(key).getContent())))
                .orElse(Collections.emptyMap());
        return SearchResultDto.builder()
                .books(books)
                .facetsByField(facetsByField)
                .numFound(searchResult.getTotalElements())
                .build();
    }

    public List<String> getSuggestions(final String query) {
        return solrClient.getSuggestions(query);
    }

}
