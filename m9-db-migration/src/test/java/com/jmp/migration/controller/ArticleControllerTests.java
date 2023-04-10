package com.jmp.migration.controller;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jmp.migration.dto.ArticleDto;
import com.jmp.migration.dto.ArticleRequestDto;
import com.jmp.migration.dto.AuthorDto;
import com.jmp.migration.model.Article;
import com.jmp.migration.model.Author;
import com.jmp.migration.repository.ArticleRepository;
import com.jmp.migration.repository.AuthorRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArticleControllerTests {

    private static final String URL = "/api/v1/articles";

    private static final String TITLE = "title";

    private static final String TEXT = "text";

    private static final String AUTHOR = "author";

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private ArticleRepository articleRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void addArticle() {
        // given
        final var article = buildArticle();
        final var request = new ArticleRequestDto(TITLE, TEXT, AUTHOR);
        when(authorRepository.findByName(AUTHOR)).thenReturn(Optional.empty());
        when(articleRepository.save(any())).thenReturn(article);
        // when
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @SneakyThrows
    @Test
    void getArticles() {
        // given
        final var article = buildArticle();
        when(articleRepository.findAll()).thenReturn(List.of(article));
        // when
        mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(buildArticleDto()))));

    }

    private Article buildArticle() {
        return Article.builder()
                .id(1L)
                .title(TITLE)
                .text(TEXT)
                .author(new Author(1L, AUTHOR))
                .build();
    }

    private ArticleDto buildArticleDto() {
        return ArticleDto.builder()
                .id(1L)
                .title(TITLE)
                .text(TEXT)
                .author(new AuthorDto(1L, AUTHOR))
                .build();
    }

}
