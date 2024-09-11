package com.bombombom.devs.core.exception;


import lombok.Getter;

@Getter
public abstract class AbstractException extends RuntimeException {

    protected final ErrorCode errorCode;

    public AbstractException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorResponse errorResponse() {
        return new ErrorResponse(this.errorCode.getCode(), getMessage());
    }
}
