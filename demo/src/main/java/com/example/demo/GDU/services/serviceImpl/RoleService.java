package com.example.demo.GDU.services.serviceImpl;

import com.example.demo.GDU.dto.role.RoleResponse;
import com.example.demo.GDU.entity.Permission;
import com.example.demo.GDU.entity.Role;
import com.example.demo.GDU.exeption.PermissionNotFoundException;
import com.example.demo.GDU.exeption.RoleNotFoundException;
import com.example.demo.GDU.mappers.RoleMapper;
import com.example.demo.GDU.repository.PermissionRepository;
import com.example.demo.GDU.repository.RoleRepository;
import com.example.demo.GDU.services.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;

    @Override
    public RoleResponse activerRole(Long id, Boolean actif) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + id));
        role.setActif(actif);
        return roleMapper.roleToRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse assignerPermissions(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + roleId));
        Set<Permission> permissions = permissionIds.stream()
                .map(pid -> permissionRepository.findById(pid)
                        .orElseThrow(() -> new PermissionNotFoundException("Permission introuvable avec l'id : " + pid)))
                .collect(Collectors.toSet());
        role.getPermissions().addAll(permissions);
        log.info("{} permission(s) ajoutée(s) au rôle '{}'", permissions.size(), role.getNom());
        return roleMapper.roleToRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse retirerPermission(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException("Permission introuvable avec l'id : " + permissionId));
        role.getPermissions().remove(permission);
        return roleMapper.roleToRoleResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleMapper.rolesToRoleResponseList(roleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllActiveRoles() {
        return roleMapper.rolesToRoleResponseList(roleRepository.findByActif(true));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        return roleMapper.roleToRoleResponse(
                roleRepository.findById(id)
                        .orElseThrow(() -> new RoleNotFoundException("Rôle introuvable avec l'id : " + id)));
    }
}