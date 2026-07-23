package com.example.demo.GDU.controllers;

import com.example.demo.GDU.dto.permission.PermissionResponse;
import com.example.demo.GDU.services.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermission());
    }

    @GetMapping("/actives")
    public ResponseEntity<List<PermissionResponse>> getAllPermissionsActives() {
        return ResponseEntity.ok(permissionService.getAllPermissionActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getById(id));
    }

    @PatchMapping("/{id}/activation")
    public ResponseEntity<PermissionResponse> activerPermission(@PathVariable Long id, @RequestParam Boolean actif) {
        return ResponseEntity.ok(permissionService.activePermission(id, actif));
    }
}