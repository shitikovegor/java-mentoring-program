package com.jmp.elastic.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logger")
public class LoggerProperties {

    private String appNameKey;

    private String appVersionKey;

    private String hostnameKey;

    private String requestIdKey;

}
