package com.bombombom.devs.external.global.exception;

import com.bombombom.devs.core.exception.AbstractException;
import com.bombombom.devs.core.exception.DetailedErrorResponse;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ErrorResponse;
import com.bombombom.devs.core.exception.ServerInternalException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Assert;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractException.class)
    protected ResponseEntity<ErrorResponse> handle(AbstractException e) {
        ErrorCode errorCode = e.getErrorCode();
        int status = errorCode.getStatusCode().getValue();

        HttpStatus httpStatus = HttpStatus.resolve(status);
        Assert.notNull(httpStatus);

        return new ResponseEntity<>(e.errorResponse(), httpStatus);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handle(RuntimeException runtimeException) {
        log.error("Unexpected Error Occur: ", runtimeException);

        ServerInternalException e = new ServerInternalException(ErrorCode.UNEXPECTED_EXCEPTION);

        return new ResponseEntity<>(e.errorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<DetailedErrorResponse> handleInvalidDtoField(
        MethodArgumentNotValidException e) throws JsonProcessingException {

        Map<String, String> errorDetails = new HashMap<>();

        e.getFieldErrors()
            .forEach(action -> errorDetails.put(action.getField(), action.getDefaultMessage()));

        DetailedErrorResponse detailedErrorResponse = new DetailedErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getFieldErrors().getFirst().getDefaultMessage(),
            errorDetails
        );
        return ResponseEntity.badRequest().body(detailedErrorResponse);
    }
}
