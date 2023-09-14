package com.japhet_sebastian.vo;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class DateMapper {
    public String asString(LocalDateTime localDateTime) {
        return localDateTime != null
                ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime) : null;
    }

    public LocalDateTime asLocalDateTime(String localDateTimeStr) {
        return localDateTimeStr != null
                ? LocalDateTime.parse(localDateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
    }
}
