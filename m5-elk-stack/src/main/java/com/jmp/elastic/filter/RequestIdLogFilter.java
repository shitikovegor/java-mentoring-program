package com.jmp.elastic.filter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Data;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

@Data
@Order(1)
public class RequestIdLogFilter extends OncePerRequestFilter {

    private final String requestIdKey;

    private final Map<String, String> appDescription;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
            final FilterChain filterChain) throws ServletException, IOException {

        try {
            final var requestId = UUID.randomUUID().toString();
            MDC.put(requestIdKey, requestId);
            appDescription.forEach(MDC::put);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }

    }

}
