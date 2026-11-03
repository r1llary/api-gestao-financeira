package com.beca.usuarios.adapter.input.rest.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.coyote.Response;
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
            NoSuchElementException exception){
        return ResponseEntity
                .status(404)
                .body("User not found");
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
}
