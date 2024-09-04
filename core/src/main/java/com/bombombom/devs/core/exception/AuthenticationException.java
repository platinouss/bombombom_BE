package com.bombombom.devs.core.exception;

public class AuthenticationException extends AbstractException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
