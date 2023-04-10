package com.jmp.search.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jmp.search.configuration.properties.BookProperties;
import com.jmp.search.exception.BookException;
import com.jmp.search.exception.ErrorCode;
import com.jmp.search.model.Book;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadBookClient {

    private final BookProperties properties;

    private final EpubReader reader;

    public List<Book> getBooks() {
        final var localStorage = new File(properties.getPath());
        if (localStorage.isFile() || !localStorage.isDirectory()) {
            throw new BookException("Directory not found", ErrorCode.DIRECTORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return Arrays.stream(localStorage.listFiles())
                .map(this::parseBook)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<Book> parseBook(final File file) {
        Book book = null;
        try {
            final var inputStream = new FileInputStream(file);
            final var importedBook = reader.readEpub(inputStream);
            final var metadata = importedBook.getMetadata();
            final var authors = metadata.getAuthors().stream()
                    .map(Author::toString)
                    .collect(Collectors.toList());
            final var spine = importedBook.getSpine();
            final var content = spine.getSpineReferences().stream()
                    .map(this::parseContent)
                    .flatMap(Optional::stream)
                    .reduce("", (first, second) -> first + second);
            book = Book.builder()
                    .id(UUID.randomUUID().toString())
                    .title(metadata.getFirstTitle())
                    .authors(authors)
                    .content(content)
                    .language(metadata.getLanguage())
                    .build();
        } catch (final IOException e) {
            log.error("File {} is not imported", file.getName(), e);
        }
        return Optional.ofNullable(book);
    }

    private Optional<String> parseContent(final SpineReference reference) {
        String content = null;
        final ByteArrayInputStream arrayInputStream;
        try {
            arrayInputStream = new ByteArrayInputStream(reference.getResource().getData());
            final var doc = Jsoup.parse(arrayInputStream, StandardCharsets.UTF_8.name(), "");
            content = doc.firstElementChild().html();
        } catch (final IOException e) {
            log.error("Error in time of parsing  of {}", reference.getResourceId(), e);
        }
        return Optional.ofNullable(content);
    }

}
