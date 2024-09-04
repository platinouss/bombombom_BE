package com.bombombom.devs.core.exception;

import java.util.Map;

public class InvalidInputException extends DetailedException {


    public InvalidInputException(ErrorCode errorCode, Map<String, String> errorDetails) {
        super(errorCode, errorDetails);
    }
}
