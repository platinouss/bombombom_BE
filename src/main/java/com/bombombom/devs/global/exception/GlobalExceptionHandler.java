package com.bombombom.devs.global.exception;

import com.bombombom.devs.user.exception.ExistUsernameException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        ExistUsernameException.class
    })
    protected ResponseEntity<ErrorResponse> handleInvalidData(RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            e.getMessage()
        );
        return ResponseEntity.badRequest().body(errorResponse);
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
