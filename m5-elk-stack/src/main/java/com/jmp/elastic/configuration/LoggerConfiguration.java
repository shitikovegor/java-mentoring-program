package com.jmp.elastic.configuration;

import java.net.InetAddress;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jmp.elastic.configuration.properties.LoggerProperties;
import com.jmp.elastic.filter.AppMDCServletFilter;
import com.jmp.elastic.filter.RequestIdLogFilter;

@Configuration
@RequiredArgsConstructor
public class LoggerConfiguration {

    @Value("${spring.application.name}")
    private String name;

    @Value("${spring.application.version}")
    private String version;

    private final LoggerProperties properties;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        final var appDescription = Map.of(properties.getAppNameKey(), name, properties.getAppVersionKey(), version,
                properties.getHostnameKey(),
                InetAddress.getLoopbackAddress().getHostName());
        final var registrationBean = new FilterRegistrationBean<>();
        final var logFilter = new RequestIdLogFilter(properties.getRequestIdKey(), appDescription);
        registrationBean.setFilter(logFilter);
        registrationBean.setFilter(new AppMDCServletFilter(appDescription, properties.getRequestIdKey()));
        return registrationBean;
    }

}
