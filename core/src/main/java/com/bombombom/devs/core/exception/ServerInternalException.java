package com.bombombom.devs.core.exception;

public class ServerInternalException extends AbstractException {

    public ServerInternalException(ErrorCode errorCode) {
        super(errorCode);
    }
}
