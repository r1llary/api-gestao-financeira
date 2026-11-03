package com.beca.usuarios.adapter.input.rest.exception;

import com.beca.usuarios.core.usuario.exception.EmailAlreadyExistsException;
import com.beca.usuarios.core.usuario.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

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
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(
            UserNotFoundException exception){
        return ResponseEntity
                .status(404)
                .body(exception.getMessage());
    }

    @ApiResponse(
            responseCode = "404",
            description = "Invalid request data",
            content = @Content(
                    mediaType = "text/plain",
                    schema = @Schema(implementation = String.class)
            )
    )
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(
            MethodArgumentNotValidException exception){
        return ResponseEntity
                .status(400)
                .body("Invalid user data");
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
            description = "Emailalready registered",
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
