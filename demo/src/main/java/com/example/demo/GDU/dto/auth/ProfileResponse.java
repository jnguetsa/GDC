package com.example.demo.GDU.dto.auth;

import com.example.demo.GDU.dto.role.RoleResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {

    private Long id;
    private String email;
    private boolean compteActif;
    private LocalDateTime derniereConnexion;
    private LocalDateTime dateCreation;
    private List<RoleResponse> roles;
}