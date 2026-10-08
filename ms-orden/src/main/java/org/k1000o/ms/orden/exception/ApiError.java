package org.k1000o.ms.orden.exception;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ApiError(
        String path,
        String message,
        int statusCode,
        LocalDateTime localDateTime,
        Map<String, String> errors
) {
}