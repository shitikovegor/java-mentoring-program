package com.jmp.elastic.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmp.elastic.TestMockData;
import com.jmp.elastic.client.EventClient;
import com.jmp.elastic.exception.EventException;
import com.jmp.elastic.mapper.EventMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTests {

    @Mock
    private EventClient client;

    private EventService service;

    @BeforeEach
    void setUp() {
        final var mapper = Mappers.getMapper(EventMapper.class);
        service = new EventService(client, mapper);
    }

    @Test
    void createEvent() {
        // given
        final var eventRequestDto = TestMockData.buildRequestDto();
        final var event = TestMockData.buildEvent(false);
        doNothing().when(client).createEvent(event);
        // when
        service.createEvent(eventRequestDto);
        // then
        Mockito.verify(client).createEvent(event);
    }

    @Test
    void getEvent() {
        // given
        final var event = TestMockData.buildEvent(true);
        final var expected = TestMockData.buildEventDto();
        when(client.getEvent(TestMockData.ID)).thenReturn(Optional.of(event));
        // when
        final var result = service.getEvent(TestMockData.ID);
        // then
        assertThat(result).isNotNull().isEqualTo(expected);
    }

    @Test
    void updateEvent_Patch() {
        // given
        final var patchRequestDto = TestMockData.buildPatchRequestDto();
        final var event = TestMockData.buildEvent(false);
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).updateEvent(TestMockData.ID, event);
        // when
        service.updateEvent(TestMockData.ID, patchRequestDto);
        // then
        Mockito.verify(client).existsEvent(TestMockData.ID);
        Mockito.verify(client).updateEvent(TestMockData.ID, event);
    }

    @Test
    void updateEvent_NotFound_ThrowException() {
        // given
        final var patchRequestDto = TestMockData.buildPatchRequestDto();
        when(client.existsEvent(TestMockData.ID)).thenReturn(false);
        // then
        assertThatThrownBy(() -> service.updateEvent(TestMockData.ID, patchRequestDto))
                .isInstanceOf(EventException.class)
                .hasMessageContaining("not found");
        Mockito.verify(client).existsEvent(TestMockData.ID);
        Mockito.verify(client, never()).updateEvent(any(), any());
    }

    @Test
    void updateEvent_Put() {
        // given
        final var requestDto = TestMockData.buildRequestDto();
        final var event = TestMockData.buildEvent(false);
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).updateEvent(TestMockData.ID, event);
        // when
        service.updateEvent(TestMockData.ID, requestDto);
        // then
        Mockito.verify(client).existsEvent(TestMockData.ID);
        Mockito.verify(client).updateEvent(TestMockData.ID, event);
    }

    @Test
    void deleteEvent() {
        // given
        when(client.existsEvent(TestMockData.ID)).thenReturn(true);
        doNothing().when(client).deleteEvent(TestMockData.ID);
        // when
        service.deleteEvent(TestMockData.ID);
        // then
        Mockito.verify(client).existsEvent(TestMockData.ID);
        Mockito.verify(client).deleteEvent(TestMockData.ID);
    }

    @Test
    void searchEvents() {
        // given
        final var searchRequestDto = TestMockData.buildSearchRequestDto();
        final var expected = TestMockData.buildEventDto();
        final var event = TestMockData.buildEvent(true);
        when(client.searchEvents(any())).thenReturn(List.of(event));
        // when
        final var result = service.searchEvents(searchRequestDto);
        // then
        assertThat(result).isNotEmpty().hasSize(1)
                .first().satisfies(element -> assertThat(element).isEqualTo(expected));
    }

    @Test
    void deleteEventByTitle() {
        // given
        doNothing().when(client).deleteEventsByTitle(TestMockData.TITLE);
        // when
        service.deleteEventByTitle(TestMockData.TITLE);
        // then
        Mockito.verify(client).deleteEventsByTitle(TestMockData.TITLE);
    }

}
