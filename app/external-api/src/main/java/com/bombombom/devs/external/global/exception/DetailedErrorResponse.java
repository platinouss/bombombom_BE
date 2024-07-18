package com.bombombom.devs.external.global.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record DetailedErrorResponse(LocalDateTime timestamp, int status, String message,
                                    Map<String, String> errorDetails) {

    public DetailedErrorResponse(int status, String message, Map<String, String> errorDetails) {
        this(LocalDateTime.now(), status, message, errorDetails);
    }
}
