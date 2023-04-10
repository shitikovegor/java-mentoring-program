package com.jmp.elastic.service;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmp.elastic.TestMockData;
import com.jmp.elastic.client.IndexClient;
import com.jmp.elastic.mapper.IndexMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexServiceTests {

    @Mock
    private IndexClient client;

    private final ObjectMapper objectMapper = TestMockData.buildObjectMapper();

    private IndexService service;

    @BeforeEach
    void setUp() {
        final var mapper = Mappers.getMapper(IndexMapper.class);
        service = new IndexService(client, objectMapper, mapper);
    }

    @Test
    void createIndex() {
        // given
        final var indexRequestDto = TestMockData.buildIndexRequestDto();
        doNothing().when(client).createIndex(TestMockData.INDEX_NAME);
        // when
        service.createIndex(indexRequestDto);
        // then
        Mockito.verify(client).createIndex(TestMockData.INDEX_NAME);
    }

    @SneakyThrows
    @Test
    void addMapping() {
        // given
        final var indexRequestDto = TestMockData.buildIndexRequestDto();
        final var mappingString = objectMapper.writeValueAsString(indexRequestDto.getMappings());
        doNothing().when(client).updateMapping(TestMockData.INDEX_NAME, mappingString);
        // when
        service.addMapping(indexRequestDto);
        // then
        Mockito.verify(client).updateMapping(TestMockData.INDEX_NAME, mappingString);
    }

    @Test
    void getIndices() {
        // given
        final var mappingDto = TestMockData.buildIndexMappingDto();
        final var mapping = TestMockData.buildIndexMapping();
        when(client.getIndices()).thenReturn(Map.of(TestMockData.INDEX_NAME, mapping));
        // when
        final var indices = service.getIndices();
        // then
        assertThat(indices).isNotEmpty().isEqualTo(Map.of(TestMockData.INDEX_NAME, mappingDto));
    }

}
