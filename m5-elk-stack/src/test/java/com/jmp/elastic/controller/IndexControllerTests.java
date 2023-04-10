package com.jmp.elastic.controller;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.jmp.elastic.TestMockData;
import com.jmp.elastic.client.EventClient;
import com.jmp.elastic.client.IndexClient;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IndexControllerTests {

    public static final String URL = "/api/v1/index";

    @MockBean
    private EventClient eventClient;

    @MockBean
    private IndexClient client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createIndex() {
        // given
        final var indexRequestDto = TestMockData.buildIndexRequestDto();
        doNothing().when(client).createIndex(TestMockData.INDEX_NAME);
        // when
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(indexRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @SneakyThrows
    @Test
    void addMapping() {
        // given
        final var indexRequestDto = TestMockData.buildIndexRequestDto();
        final var mappingString = mapper.writeValueAsString(indexRequestDto.getMappings());
        doNothing().when(client).updateMapping(TestMockData.INDEX_NAME, mappingString);
        // when
        mockMvc.perform(post(URL + "/mappings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(indexRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @SneakyThrows
    @Test
    void getIndices() {
        // given
        final var mappingDto = TestMockData.buildIndexMappingDto();
        final var mapping = TestMockData.buildIndexMapping();
        when(client.getIndices()).thenReturn(Map.of(TestMockData.INDEX_NAME, mapping));
        // when
        mockMvc.perform(get(URL)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Map.of(TestMockData.INDEX_NAME, mappingDto))));
    }

}
