package com.beca.transacoes.adapter.input.rest.exception;

import com.beca.transacoes.core.conta.exception.AccountNotFoundException;
import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handlerNotFound(
            NoSuchElementException exception) {

        return ResponseEntity.status(404)
                .body(exception.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handlerForbidden(
            SecurityException exception) {

        return ResponseEntity.status(403)
                .body(exception.getMessage());
    }


    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handlerAccountNotFound(
            AccountNotFoundException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                404,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(404).body(response);
    }


    @ExceptionHandler(AccountServiceUnavailableException.class)
    public ResponseEntity<String> handlerAccountServiceUnavailable(
            AccountServiceUnavailableException exception) {

        return ResponseEntity.status(503)
                .body(exception.getMessage());

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