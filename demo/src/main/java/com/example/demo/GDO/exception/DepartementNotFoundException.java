package com.example.demo.GDO.exception;

public class DepartementNotFoundException extends RuntimeException {
    public DepartementNotFoundException(String message) {
        super(message);
    }
}