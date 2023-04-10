package com.jmp.search.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SolrDocument(collection = "books")
public class Book {

    @Id
    @Field
    private String id;

    @Field
    private String title;

    @Field
    private List<String> authors;

    @Field
    private String content;

    @Field
    private String language;

}
