package com.example.aliyevgrocery.exception;

public class NumberAlreadyExistsException extends RuntimeException {
    public NumberAlreadyExistsException(String message) {
        super(message);
    }
}
