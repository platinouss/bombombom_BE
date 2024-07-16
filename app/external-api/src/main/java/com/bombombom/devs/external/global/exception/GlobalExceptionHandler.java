package com.bombombom.devs.external.global.exception;

import com.bombombom.devs.client.naver.exception.ExternalApiException;
import com.bombombom.devs.external.study.exception.NotFoundException;
import com.bombombom.devs.external.user.exception.ExistUsernameException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        ExistUsernameException.class,
        ExternalApiException.class
    })
    protected ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(
        NotFoundException.class
    )
    protected ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            e.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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
