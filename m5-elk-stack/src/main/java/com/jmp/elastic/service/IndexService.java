package com.jmp.elastic.service;

import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.jmp.elastic.client.IndexClient;
import com.jmp.elastic.dto.index.IndexMappingDto;
import com.jmp.elastic.dto.index.IndexRequestDto;
import com.jmp.elastic.exception.ErrorCode;
import com.jmp.elastic.exception.IndexException;
import com.jmp.elastic.mapper.IndexMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexService {

    private final IndexClient client;

    private final ObjectMapper objectMapper;

    private final IndexMapper mapper;

    public void createIndex(final IndexRequestDto indexRequest) {
        client.createIndex(indexRequest.getIndex());
        if (indexRequest.getMappings() != null) {
            addMapping(indexRequest);
        }
    }

    public void addMapping(final IndexRequestDto mappingRequest) {
        try {
            final var mappingString = objectMapper.writeValueAsString(mappingRequest.getMappings());
            client.updateMapping(mappingRequest.getIndex(), mappingString);
        } catch (final JsonProcessingException e) {
            throw new IndexException("Invalid mapping structure", ErrorCode.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST, e);
        }
    }

    public Map<String, IndexMappingDto> getIndices() {
        return client.getIndices().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> mapper.toMappingDto(e.getValue())));
    }

}
