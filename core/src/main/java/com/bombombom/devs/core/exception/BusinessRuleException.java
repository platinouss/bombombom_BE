package com.bombombom.devs.core.exception;

public class BusinessRuleException extends AbstractException {

    public BusinessRuleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
