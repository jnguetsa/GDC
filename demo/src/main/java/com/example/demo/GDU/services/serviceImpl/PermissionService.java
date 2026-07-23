package com.example.demo.GDU.services.serviceImpl;

import com.example.demo.GDU.dto.permission.PermissionResponse;
import com.example.demo.GDU.entity.Permission;
import com.example.demo.GDU.exeption.PermissionNotFoundException;
import com.example.demo.GDU.mappers.PermissionMapper;
import com.example.demo.GDU.repository.PermissionRepository;
import com.example.demo.GDU.services.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionService implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermission() {
        return permissionMapper.toPermissionResponseList(permissionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissionActive() {
        return permissionMapper.toPermissionResponseList(permissionRepository.findByActif(true));
    }

    @Override
    public PermissionResponse activePermission(Long id, Boolean actif) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionNotFoundException("Permission introuvable avec l'id : " + id));
        permission.setActif(actif);
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(Long id) {
        return permissionMapper.toPermissionResponse(
                permissionRepository.findById(id)
                        .orElseThrow(() -> new PermissionNotFoundException("Permission introuvable avec l'id : " + id)));
    }
}