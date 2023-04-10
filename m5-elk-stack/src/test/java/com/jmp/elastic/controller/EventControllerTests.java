package com.jmp.elastic.controller;

import java.util.List;
import java.util.Optional;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerTests {

    public static final String URL = "/api/v1/events";

    @MockBean
    private EventClient client;

    @MockBean
    private IndexClient indexClient;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    void createEvent() {
        // given
        final var eventRequestDto = TestMockData.buildRequestDto();
        doNothing().when(client).createEvent(any());
        // when
        mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(eventRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @SneakyThrows
    @Test
    void getEvent() {
        // given
        final var event = TestMockData.buildEvent(true);
        when(client.getEvent(TestMockData.ID)).thenReturn(Optional.of(event));
        // when
        mockMvc.perform(get(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(TestMockData.buildJson("mock/event.json")));
    }

    @SneakyThrows
    @Test
    void getEvent_NotFound() {
        // given
        when(client.getEvent(TestMockData.ID)).thenReturn(Optional.empty());
        // when
        mockMvc.perform(get(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateEvent_Patch() {
        // given
        final var patchRequestDto = TestMockData.buildPatchRequestDto();
        final var event = TestMockData.buildEvent(false);
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).updateEvent(TestMockData.ID, event);
        // when
        mockMvc.perform(patch(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(patchRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SneakyThrows
    @Test
    void updateEvent_Patch_NotFound() {
        // given
        final var patchRequestDto = TestMockData.buildPatchRequestDto();
        when(client.existsEvent(TestMockData.ID)).thenReturn(false);
        // when
        mockMvc.perform(patch(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(patchRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateEventPut() {
        // given
        final var requestDto = TestMockData.buildRequestDto();
        final var event = TestMockData.buildEvent(false);
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).updateEvent(TestMockData.ID, event);
        // when
        mockMvc.perform(put(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteEvent() {
        // given
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).deleteEvent(TestMockData.ID);
        // when
        mockMvc.perform(delete(URL + "/" + TestMockData.ID)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @SneakyThrows
    @Test
    void deleteByTitle() {
        // given
        doNothing().when(client).deleteEventsByTitle(TestMockData.TITLE);
        // when
        mockMvc.perform(delete(URL + "?title=" + TestMockData.TITLE)
                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @SneakyThrows
    @Test
    void searchEvent() {
        // given
        final var searchRequestDto = TestMockData.buildSearchRequestDto();
        final var expected = List.of(TestMockData.buildEventDto());
        final var event = TestMockData.buildEvent(true);
        when(client.searchEvents(any())).thenReturn(List.of(event));
        // when
        mockMvc.perform(post(URL + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(searchRequestDto)))
                // then
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

}
