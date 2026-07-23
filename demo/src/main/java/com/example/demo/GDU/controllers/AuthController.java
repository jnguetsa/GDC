package com.example.demo.GDU.controllers;

import com.example.demo.GDU.dto.auth.*;
import com.example.demo.GDU.services.serviceImpl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profil")
    public ResponseEntity<ProfileResponse> getProfil(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getProfil(userDetails.getUsername()));
    }

    @PatchMapping("/activation")
    public ResponseEntity<ProfileResponse> activerCompte(@Valid @RequestBody ActivationRequest request) {
        return ResponseEntity.ok(authService.activerCompte(request));
    }

    @PostMapping("/assigner-roles")
    public ResponseEntity<ProfileResponse> assignerRoles(@Valid @RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(authService.assignerRoles(request));
    }
}