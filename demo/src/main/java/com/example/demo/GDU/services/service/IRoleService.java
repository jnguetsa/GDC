package com.example.demo.GDU.services.service;

import com.example.demo.GDU.dto.role.RoleResponse;

import java.util.List;
import java.util.Set;

public interface IRoleService {
    RoleResponse activerRole(Long id, Boolean actif);
    RoleResponse assignerPermissions(Long roleId, Set<Long> permissionIds);
    RoleResponse retirerPermission(Long roleId, Long permissionId);
    List<RoleResponse> getAllRoles();
    List<RoleResponse> getAllActiveRoles();
    RoleResponse getById(Long id);
}