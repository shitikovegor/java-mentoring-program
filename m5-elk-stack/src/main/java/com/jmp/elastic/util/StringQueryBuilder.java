package com.jmp.elastic.util;

import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class StringQueryBuilder {

    public String buildQuery(final String query) {
        return "{ \"query\": " + query + " }";
    }

    public String buildRangeQuery(final String fieldName, final String value) {
        return "{ \"range\": { \"" + fieldName +
                "\": { \"gte\": \"" + value + "\", \"lt\": \"now\"}}}";
    }

    public String buildMatchQuery(final String fieldName, final String value) {
        return "{ \"match\": { \"" + fieldName + "\": \"" + value + "\"}}";
    }

    public String buildBooleanQuery(final List<String> searchQueries) {
        final var stringSearchQueries = String.join(",", searchQueries);
        return "{ \"bool\": { \"must\": [" + stringSearchQueries + " ]}}";
    }

    public String buildUpdateQuery(final String updateQuery) {
        return "{\"doc\": " + updateQuery + "}";
    }

    public String buildQueryWithMatch(final String fieldName, final String value) {
        final var matchQuery = buildMatchQuery(fieldName, value);
        return buildQuery(matchQuery);
    }

}
