package com.bombombom.devs.core.exception;

public class DuplicationException extends AbstractException {

    public DuplicationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
