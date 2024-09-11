package com.bombombom.devs.core.exception;

public class InvalidInputException extends AbstractException {


    public InvalidInputException(ErrorCode errorCode) {
        super(errorCode);
    }
}
