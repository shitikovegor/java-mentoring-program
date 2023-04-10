package com.jmp.elastic.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "events")
public class EventProperties {

    private String indexName;

}
