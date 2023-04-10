package com.jmp.search.configuration.properties;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "books")
public class BookProperties {

    private String path;

    private int pageLimit;

    private String collection;

    private List<String> suggestFields;

    private String suggesterName;

}
