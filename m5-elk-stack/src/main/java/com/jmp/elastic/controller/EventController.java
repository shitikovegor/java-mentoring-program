package com.jmp.elastic.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.elastic.dto.event.EventDto;
import com.jmp.elastic.dto.event.EventPatchRequestDto;
import com.jmp.elastic.dto.event.EventRequestDto;
import com.jmp.elastic.dto.search.EventSearchRequestDto;
import com.jmp.elastic.service.EventService;

@Validated
@RestController
@RequestMapping(value = "api/v1/events", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createEvent(@RequestBody @NotNull @Valid final EventRequestDto eventRequest) {
        service.createEvent(eventRequest);
    }

    @GetMapping(path = "{id}")
    public EventDto getEvent(@PathVariable @NotBlank final String id) {
        return service.getEvent(id);
    }

    @PatchMapping(path = "{id}")
    public void updateEvent(@PathVariable @NotBlank final String id,
            @RequestBody final EventPatchRequestDto eventRequest) {
        service.updateEvent(id, eventRequest);
    }

    @PutMapping(path = "{id}")
    public void updateEvent(@PathVariable @NotBlank final String id,
            @RequestBody @NotNull @Valid final EventRequestDto eventRequest) {
        service.updateEvent(id, eventRequest);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @NotBlank final String id) {
        service.deleteEvent(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByTitle(@RequestParam @NotBlank final String title) {
        service.deleteEventByTitle(title);
    }

    @PostMapping(path = "/search")
    public List<EventDto> searchEvent(@RequestBody final EventSearchRequestDto searchRequest) {
        return service.searchEvents(searchRequest);
    }

}
