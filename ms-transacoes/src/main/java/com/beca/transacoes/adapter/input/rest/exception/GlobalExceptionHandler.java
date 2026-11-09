package com.beca.transacoes.adapter.input.rest.exception;

import com.beca.transacoes.core.conta.exception.AccountNotFoundException;
import com.beca.transacoes.core.conta.exception.AccountServiceUnavailableException;
import com.beca.transacoes.core.transacao.exception.TransactionAccessDeniedException;
import com.beca.transacoes.core.transacao.exception.TransactionNotFoundException;
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
    public ResponseEntity<ValidationErrorResponse> handlerNotFound(
            NoSuchElementException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                404,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(404).body(response);
    }


    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ValidationErrorResponse> handlerForbidden(
            SecurityException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                403,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(403).body(response);
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
    public ResponseEntity<ValidationErrorResponse> handlerAccountServiceUnavailable(
            AccountServiceUnavailableException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                503,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(503).body(response);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleTransactionNotFound(
            TransactionNotFoundException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                404,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(TransactionAccessDeniedException.class)
    public ResponseEntity<ValidationErrorResponse> handleTransactionAccessDenied(
            TransactionAccessDeniedException exception) {

        ValidationErrorResponse response = new ValidationErrorResponse(
                403,
                exception.getMessage(),
                List.of()
        );

        return ResponseEntity.status(403).body(response);
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