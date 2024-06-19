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
    protected ResponseEntity<ErrorResponse> handleInvalidDtoField(
        MethodArgumentNotValidException e) throws JsonProcessingException {

        Map<String, String> errors = new HashMap<>();

        e.getFieldErrors()
            .forEach(action -> errors.put(action.getField(), action.getDefaultMessage()));

        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            objectMapper.writeValueAsString(errors)
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
}
