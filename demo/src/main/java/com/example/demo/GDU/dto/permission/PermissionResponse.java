package com.example.demo.GDU.dto.permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponse {
    private Long id;
    private String nom;
    private String description;
    private boolean actif;
}