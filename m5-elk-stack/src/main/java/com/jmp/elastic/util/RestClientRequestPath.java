package com.jmp.elastic.util;

public final class RestClientRequestPath {

    private RestClientRequestPath() {
    }

    public static final String CREATE = "/_create/";

    public static final String GET = "/_doc/";

    public static final String UPDATE = "/_update/";

    public static final String COUNT = "/_count";

    public static final String SEARCH = "/_search";

    public static final String DELETE_BY_QUERY = "/_delete_by_query";

    public static final String MAPPING = "/_mapping";

    public static final String SEPARATOR = "/";

}
