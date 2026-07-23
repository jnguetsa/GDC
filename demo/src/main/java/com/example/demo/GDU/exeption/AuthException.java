package com.example.demo.GDU.exeption;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}