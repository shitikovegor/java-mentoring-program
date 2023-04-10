package com.jmp.elastic.client;

import java.util.Map;

import com.jmp.elastic.model.IndexMapping;

public interface IndexClient {

    void createIndex(String indexName);

    void updateMapping(String index, String mappingJson);

    Map<String, IndexMapping> getIndices();

}
