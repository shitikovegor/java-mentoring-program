package com.jmp.search.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;

import com.jmp.search.dto.BookDto;
import com.jmp.search.dto.FacetDto;
import com.jmp.search.dto.SearchRequestDto;
import com.jmp.search.model.Book;
import com.jmp.search.model.SearchRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookDto toBookDto(final Book book);

    List<BookDto> toBookDtos(final List<Book> books);

    Book toBook(final BookDto bookDto);

    @Mapping(target = "field", expression = "java(searchRequestDto.getField().getName())")
    @Mapping(target = "facetField", expression = "java(searchRequestDto.getFacetField().getName())")
    SearchRequest toSearchRequest(final SearchRequestDto searchRequestDto);

    FacetDto toFacetDto(final FacetFieldEntry entry);

    List<FacetDto> toFacetDtos(final List<FacetFieldEntry> entries);

}
