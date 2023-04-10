package com.jmp.migration.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.migration.dto.ArticleDto;
import com.jmp.migration.dto.ArticleRequestDto;
import com.jmp.migration.service.ArticleService;

@RestController
@RequestMapping(path = "api/v1/articles", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDto addArticle(@RequestBody final ArticleRequestDto articleRequest) {
        return service.addArticle(articleRequest);
    }

    @GetMapping
    public List<ArticleDto> getArticles() {
        return service.getArticles();
    }

}
