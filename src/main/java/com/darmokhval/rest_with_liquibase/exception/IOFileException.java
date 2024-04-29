package com.darmokhval.rest_with_liquibase.exception;

public class IOFileException extends RuntimeException{
    public IOFileException(String message) {
        super(message);
    }
    public IOFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
