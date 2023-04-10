package com.jmp.elastic.model.rest;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jmp.elastic.model.IndexMapping;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexData {

    private IndexMapping mappings;

    @NotNull
    private String index;

}
