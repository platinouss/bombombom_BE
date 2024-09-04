package com.bombombom.devs.core.exception;

import java.util.Map;
import lombok.Getter;


@Getter
public class DetailedErrorResponse extends ErrorResponse {

    private final Map<String, String> errorDetails;

    public DetailedErrorResponse(int errorCode, String message,
        Map<String, String> errorDetails) {
        super(errorCode, message);
        this.errorDetails = errorDetails;
    }


}
