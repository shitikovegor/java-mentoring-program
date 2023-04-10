package com.jmp.elastic.dto.index;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexRequestDto {

    private IndexMappingDto mappings;

    @NotNull
    private String index;

}
