package com.example.demo.GDU.dto.permission;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivePermission {
    @NotNull(message = "L'ID de la permission est requis")
    private Long id;
    @NotNull(message = "L'état de la permission est requis")
    private Boolean actif;
}