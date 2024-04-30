package com.darmokhval.rest_with_liquibase.controller;

import com.darmokhval.rest_with_liquibase.exception.IOFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * main class to handle exceptions to return them as easy to read response with exception details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException exception, WebRequest request) {
        return formErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception, WebRequest request) {
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return formErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception, WebRequest request) {
        String errorMessage = String.format("Invalid request data: %s", exception.getMessage());
        return formErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    @ExceptionHandler(IOFileException.class)
    public ResponseEntity<Map<String, Object>> handleCSVGenerationException(
            IOFileException exception, WebRequest request) {
        return formErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request);
    }

    private ResponseEntity<Map<String, Object>> formErrorResponse(
            HttpStatus status, List<String> messages, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("messages", messages);
        responseBody.put("path", request.getDescription(false));

        return new ResponseEntity<>(responseBody, status);
    }

    private ResponseEntity<Map<String, Object>> formErrorResponse(
            HttpStatus status, String message, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", message);
        responseBody.put("path", request.getDescription(false));

        return new ResponseEntity<>(responseBody, status);
    }
}