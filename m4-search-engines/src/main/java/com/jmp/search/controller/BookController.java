package com.jmp.search.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.search.dto.BookDto;
import com.jmp.search.dto.SearchRequestDto;
import com.jmp.search.dto.SearchResultDto;
import com.jmp.search.service.BookService;

@Validated
@RestController
@RequestMapping(value = "api/v1/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookController {

    private final BookService service;

    @PostMapping(path = "/index")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void indexBooks() {
        service.indexBooks();
    }

    @PostMapping(path = "/search")
    public SearchResultDto search(@RequestBody @NotNull @Valid final SearchRequestDto searchRequest) {
        return service.searchBooks(searchRequest);
    }

    @GetMapping(path = "{id}")
    public BookDto getBook(@PathVariable @NotBlank final String id) {
        return service.getBookById(id);
    }

    @GetMapping(path = "/suggest")
    public List<String> getSuggestions(@RequestParam @NotBlank final String query) {
        return service.getSuggestions(query);
    }

}
