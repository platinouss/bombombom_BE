package com.bombombom.devs.core.exception;

import java.util.Map;

public class ExternalApiException extends DetailedException {


    public ExternalApiException(ErrorCode errorCode, Map<String, String> errorDetails) {
        super(errorCode, errorDetails);
    }

}
