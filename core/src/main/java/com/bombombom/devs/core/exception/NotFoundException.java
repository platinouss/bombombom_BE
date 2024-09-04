package com.bombombom.devs.core.exception;

public class NotFoundException extends AbstractException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
