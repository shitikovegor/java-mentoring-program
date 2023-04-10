package com.jmp.search.dto;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchFieldNameDto {

    TITLE("title"), AUTHORS("authors"), CONTENT("content"), LANGUAGE("language");

    private final String name;

    @JsonValue
    public String toJson() {
        return name().toLowerCase(Locale.ROOT);
    }

}
