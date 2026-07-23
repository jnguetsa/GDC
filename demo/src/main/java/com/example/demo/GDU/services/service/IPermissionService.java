package com.example.demo.GDU.services.service;

import com.example.demo.GDU.dto.permission.PermissionResponse;

import java.util.List;

public interface IPermissionService {
    List<PermissionResponse> getAllPermission();
    List<PermissionResponse> getAllPermissionActive();
    PermissionResponse activePermission(Long id, Boolean actif);
    PermissionResponse getById(Long id);
}