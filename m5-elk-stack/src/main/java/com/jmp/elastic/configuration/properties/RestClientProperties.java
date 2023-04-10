package com.jmp.elastic.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "client")
public class RestClientProperties {

    private String hitsKey;

    private String countKey;

    private String sourceKey;

}
