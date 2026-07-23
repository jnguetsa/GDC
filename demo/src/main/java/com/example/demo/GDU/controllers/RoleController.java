package com.example.demo.GDU.controllers;

import com.example.demo.GDU.dto.role.RoleResponse;
import com.example.demo.GDU.services.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<RoleResponse>> getAllActiveRoles() {
        return ResponseEntity.ok(roleService.getAllActiveRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getById(id));
    }

    @PatchMapping("/{id}/activation")
    public ResponseEntity<RoleResponse> activerRole(@PathVariable Long id, @RequestParam Boolean actif) {
        return ResponseEntity.ok(roleService.activerRole(id, actif));
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<RoleResponse> assignerPermissions(
            @PathVariable Long id,
            @RequestBody Set<Long> permissionIds) {
        return ResponseEntity.ok(roleService.assignerPermissions(id, permissionIds));
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ResponseEntity<RoleResponse> retirerPermission(
            @PathVariable Long id,
            @PathVariable Long permissionId) {
        return ResponseEntity.ok(roleService.retirerPermission(id, permissionId));
    }
}