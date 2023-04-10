package com.jmp.search.client;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.Suggestion;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.jmp.search.configuration.properties.BookProperties;
import com.jmp.search.exception.BookException;
import com.jmp.search.exception.ErrorCode;
import com.jmp.search.model.Book;
import com.jmp.search.model.SearchRequest;

@Component
@RequiredArgsConstructor
public class SolrBookClient {

    private final BookProperties properties;

    private final SolrTemplate template;

    public FacetPage<Book> searchBooks(final SearchRequest searchRequest) {
        final var query = new SimpleFacetQuery(new Criteria(Criteria.WILDCARD).expression(Criteria.WILDCARD));
        if (searchRequest.isFulltext()) {
            query.addCriteria(new SimpleStringCriteria(searchRequest.getQ()));
        } else {
            query.addFilterQuery(new SimpleFilterQuery().addCriteria(
                    new Criteria(searchRequest.getField()).expression(searchRequest.getValue())));
        }
        query.setFacetOptions(new FacetOptions().addFacetOnField(searchRequest.getFacetField()));
        query.setRows(properties.getPageLimit());
        query.setOffset(searchRequest.getPage());
        return template.queryForFacetPage(properties.getCollection(), query, Book.class);
    }

    public List<String> getSuggestions(final String queryToSuggest) {
        try {
            final var query = buildQuery(queryToSuggest);
            final var response = template.getSolrClient().query(properties.getCollection(), query);
            final var suggestions = response.getSuggesterResponse().getSuggestions().get(properties.getSuggesterName());
            return suggestions.stream()
                    .map(Suggestion::getTerm)
                    .collect(Collectors.toList());
        } catch (final Exception e) {
            throw new BookException("Suggestion request error: " + e.getMessage(), ErrorCode.SUGGESTION_ERROR,
                    HttpStatus.BAD_GATEWAY);
        }
    }

    private SolrQuery buildQuery(final String queryToSuggest) {
        final var query = new SolrQuery();
        query.setRequestHandler("/suggest");
        query.set("suggest", "true");
        query.set("suggest.build", "true");
        query.set("suggest.dictionary", properties.getSuggesterName());
        query.set("suggest.q", queryToSuggest);
        return query;
    }

}
