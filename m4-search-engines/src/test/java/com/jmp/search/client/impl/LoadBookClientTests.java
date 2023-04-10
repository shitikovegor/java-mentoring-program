package com.jmp.search.client.impl;

import java.io.IOException;
import java.io.InputStream;

import lombok.SneakyThrows;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.jmp.search.client.LoadBookClient;
import com.jmp.search.configuration.properties.BookProperties;
import com.jmp.search.exception.BookException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoadBookClientTests {

    private LoadBookClient client;

    @Test
    void getBooks_OneBook() {
        // given
        final var properties = new BookProperties();
        properties.setPath(this.getClass().getClassLoader().getResource("books").getPath());
        client = new LoadBookClient(properties, new EpubReader());
        // when
        final var books = client.getBooks();
        // then
        assertThat(books).isNotEmpty().hasSize(1)
                .first().satisfies(book -> {
                    assertThat(book.getTitle()).isEqualTo("Household Tales");
                    assertThat(book.getAuthors()).isNotEmpty().hasSize(2)
                            .first().satisfies(author -> assertThat(author).isEqualTo("Grimm, Jacob"));
                    assertThat(book.getLanguage()).isEqualTo("en-GB");
                    assertThat(book.getContent()).contains("<title>Titlepage</title>");
                });
    }

    @Test
    void getBooks_InvalidDirectory_Exception() {
        // given
        final var properties = new BookProperties();
        properties.setPath(this.getClass().getClassLoader().getResource("books/test-book.epub").getPath());
        client = new LoadBookClient(properties, new EpubReader());
        // when
        assertThatThrownBy(() -> client.getBooks())
                // then
                .isInstanceOf(BookException.class)
                .hasMessageContaining("Directory not found");
    }

    @SneakyThrows
    @Test
    void getBooks_ParsingError_ReturnEmptyList() {
        // given
        final var reader = Mockito.mock(EpubReader.class);
        final var properties = new BookProperties();
        properties.setPath(this.getClass().getClassLoader().getResource("books").getPath());
        client = new LoadBookClient(properties, reader);
        when(reader.readEpub(any(InputStream.class))).thenThrow(new IOException());
        // when
        final var books = client.getBooks();
        // then
        assertThat(books).isEmpty();
    }

}
