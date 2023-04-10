package com.jmp.search.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    @NotNull
    private SearchFieldNameDto field;

    @NotBlank
    private String value;

    @NotNull
    private SearchFieldNameDto facetField;

    private boolean fulltext;

    private String q;

    private long page;

}
