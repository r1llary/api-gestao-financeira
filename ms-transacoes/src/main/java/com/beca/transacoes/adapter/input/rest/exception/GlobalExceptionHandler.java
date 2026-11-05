package com.beca.transacoes.adapter.input.rest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        List<FieldValidationError> errors = new ArrayList<>();

        for (FieldError fieldError :
                exception.getBindingResult().getFieldErrors()) {

            errors.add(new FieldValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage()
            ));
        }

        ValidationErrorResponse response = new ValidationErrorResponse(
                400,
                "Invalid request data",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    public record FieldValidationError(
            String field,
            String message
    ) {
    }

    public record ValidationErrorResponse(
            int status,
            String message,
            List<FieldValidationError> errors
    ) {
    }
}