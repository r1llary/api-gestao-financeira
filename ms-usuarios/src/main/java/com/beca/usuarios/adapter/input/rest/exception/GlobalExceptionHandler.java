package com.beca.usuarios.adapter.input.rest.exception;

import com.beca.usuarios.core.usuario.exception.EmailAlreadyExistsException;
import com.beca.usuarios.core.usuario.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)
            )
    )
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(
            UserNotFoundException exception){
        return ResponseEntity
                .status(404)
                .body(exception.getMessage());
    }

    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content(
                    mediaType = "application/json"
            )
    )
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map <String, List<String>>> handleValidationError(
            MethodArgumentNotValidException exception){
                Map <String, List<String>> errors = new LinkedHashMap<>();
                for (FieldError error:exception.getBindingResult().getFieldErrors()){
                    String field = error.getField();

                    if (!errors.containsKey(field)){
                        errors.put(field, new ArrayList<>());
                    }
                    errors.get(field).add(error.getDefaultMessage());
                }
                return ResponseEntity.badRequest().body(errors);
    }

    @ApiResponse(
            responseCode = "404",
            description = "Invalid request data",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)
            )
    )
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handlerBadRequest(
            IllegalArgumentException exception){
        return ResponseEntity
                .status(400)
                .body(exception.getMessage());
    }

    @ApiResponse(
            responseCode = "409",
            description = "Email already registered",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)
            )
    )
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handlerEmailAlreadyExists(
            EmailAlreadyExistsException exception){
    return ResponseEntity
            .status(409)
            .body(exception.getMessage());
    }
}
