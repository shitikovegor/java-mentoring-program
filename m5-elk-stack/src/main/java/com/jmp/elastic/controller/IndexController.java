package com.jmp.elastic.controller;

import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jmp.elastic.dto.index.IndexMappingDto;
import com.jmp.elastic.dto.index.IndexRequestDto;
import com.jmp.elastic.service.IndexService;

@Validated
@RestController
@RequestMapping(value = "api/v1/index", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IndexController {

    private final IndexService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createIndex(@RequestBody final IndexRequestDto indexRequest) {
        service.createIndex(indexRequest);
    }

    @PostMapping(path = "/mappings")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addMapping(@RequestBody @NotNull @Valid final IndexRequestDto mappingRequest) {
        service.addMapping(mappingRequest);
    }

    @GetMapping
    public Map<String, IndexMappingDto> getIndices() {
        return service.getIndices();
    }

}
