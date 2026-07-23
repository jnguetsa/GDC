package com.example.demo.GDU.exeption;

import jakarta.validation.constraints.NotBlank;

public class EmployeNotFoundException extends RuntimeException {
    public EmployeNotFoundException(@NotBlank(message = "L'email est requis") String message) {
        super(message);
    }
}
