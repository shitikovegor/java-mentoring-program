package com.jmp.search.converter;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.jmp.search.dto.SearchFieldNameDto;

@Component
public class SearchFieldNameDtoConverter implements Converter<String, SearchFieldNameDto> {

    @Override
    public SearchFieldNameDto convert(final String value) {
        return SearchFieldNameDto.valueOf(value.toUpperCase(Locale.ROOT));
    }

}
