package com.example.aliyevgrocery.exception;

public class UserProductNotFoundException extends RuntimeException {
    public UserProductNotFoundException(String message) {
        super(message);
    }
}
