package com.example.demo.GDU.dto.role;

import com.example.demo.GDU.dto.permission.PermissionResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleResponse {
    private Long id;
    private String nom;
    private String description;
    private boolean actif;
    private Set<PermissionResponse> permissions;
}
