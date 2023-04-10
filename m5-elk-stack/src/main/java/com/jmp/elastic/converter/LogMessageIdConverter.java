package com.jmp.elastic.converter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.Data;

@Data
public class LogMessageIdConverter extends ClassicConverter {

    @Override
    public String convert(final ILoggingEvent event) {
        final var level = event.getLevel();
        return switch (level.levelInt) {
            case Level.DEBUG_INT -> "00-00-10000";
            case Level.INFO_INT -> "00-00-20000";
            case Level.WARN_INT -> "00-00-30000";
            case Level.ERROR_INT -> "00-00-40000";
            case Level.TRACE_INT -> "00-00-50000";
            default -> "00-01-00000";
        };
    }

}
