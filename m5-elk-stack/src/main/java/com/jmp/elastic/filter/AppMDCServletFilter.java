package com.jmp.elastic.filter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

@Order(1)
public class AppMDCServletFilter extends MDCInsertingServletFilter {

    private final Map<String, String> applicationData;

    private final String requestIdKey;

    public AppMDCServletFilter(final Map<String, String> applicationData, final String requestIdKey) {
        this.applicationData = applicationData;
        this.requestIdKey = requestIdKey;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            final var requestId = UUID.randomUUID().toString();
            MDC.put(requestIdKey, requestId);
            applicationData.forEach(MDC::put);
            super.doFilter(request, response, chain);
        } finally {
            MDC.remove(requestIdKey);
            applicationData.forEach((key, value) -> MDC.remove(key));
        }

    }

}
