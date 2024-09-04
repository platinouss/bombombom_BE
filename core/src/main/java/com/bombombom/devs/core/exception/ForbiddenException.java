package com.bombombom.devs.core.exception;

public class ForbiddenException extends AbstractException {


    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
