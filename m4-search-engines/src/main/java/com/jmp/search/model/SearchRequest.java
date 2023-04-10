package com.jmp.search.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String field;

    private String value;

    private String facetField;

    private boolean fulltext;

    private String q;

    private long page;

}
