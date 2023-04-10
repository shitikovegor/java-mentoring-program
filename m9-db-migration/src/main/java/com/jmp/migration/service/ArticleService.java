package com.jmp.migration.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.jmp.migration.dto.ArticleDto;
import com.jmp.migration.dto.ArticleRequestDto;
import com.jmp.migration.dto.AuthorDto;
import com.jmp.migration.model.Article;
import com.jmp.migration.model.Author;
import com.jmp.migration.repository.ArticleRepository;
import com.jmp.migration.repository.AuthorRepository;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository repository;

    private final AuthorRepository authorRepository;

    public ArticleDto addArticle(final ArticleRequestDto articleRequest) {
        final var author = authorRepository.findByName(articleRequest.getAuthorName())
                .orElse(Author.builder()
                        .name(articleRequest.getAuthorName()).build());
        final var article = Article.builder()
                .title(articleRequest.getTitle())
                .text(articleRequest.getText())
                .author(author)
                .build();
        final var savedArticle = repository.save(article);
        return buildDto(savedArticle);
    }

    public List<ArticleDto> getArticles() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(this::buildDto)
                .collect(Collectors.toList());
    }

    private ArticleDto buildDto(final Article article) {
        return ArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .text(article.getText())
                .author(AuthorDto.builder()
                        .id(article.getAuthor().getId())
                        .name(article.getAuthor().getName()).build())
                .build();
    }

}
