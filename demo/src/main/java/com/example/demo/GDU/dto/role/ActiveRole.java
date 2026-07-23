package com.example.demo.GDU.dto.role;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveRole {
    @NotNull(message = "L'ID du rôle est requis")
    private Long id;
    @NotNull(message = "L'état du rôle est requis")
    private Boolean actif;
}