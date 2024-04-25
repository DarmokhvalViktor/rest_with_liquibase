package com.darmokhval.rest_with_liquibase.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Get error messages from the binding result
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage) // Get the default message for each field error
                .collect(Collectors.toList());

        return formErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, request);
    }

    private ResponseEntity<Map<String, Object>> formErrorResponse(
            HttpStatus status, List<String> messages, WebRequest request) {
        // Create a response body with basic details and the list of validation error messages
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("messages", messages); // List of validation error messages
        responseBody.put("path", request.getDescription(false)); // URI that caused the error

        return new ResponseEntity<>(responseBody, status);
    }

    // Overloaded method to handle different message types (String, List<String>)
    private ResponseEntity<Map<String, Object>> formErrorResponse(
            HttpStatus status, String message, WebRequest request) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("timestamp", System.currentTimeMillis());
        responseBody.put("status", status.value());
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", message); // Single message (e.g., from IllegalArgumentException)
        responseBody.put("path", request.getDescription(false));

        return new ResponseEntity<>(responseBody, status);
    }
}