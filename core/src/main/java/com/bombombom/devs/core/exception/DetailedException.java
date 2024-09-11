package com.bombombom.devs.core.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class DetailedException extends AbstractException {

    private final Map<String, String> errorDetails;

    public DetailedException(ErrorCode errorCode, Map<String, String> errorDetails) {
        super(errorCode);
        this.errorDetails = errorDetails;
    }

    @Override
    public ErrorResponse errorResponse() {
        return new DetailedErrorResponse(
            errorCode.getCode(),
            errorCode.getMessage(),
            errorDetails
        );
    }
}
