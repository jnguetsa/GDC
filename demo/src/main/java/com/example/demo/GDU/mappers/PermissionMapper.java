package com.example.demo.GDU.mappers;

import com.example.demo.GDU.dto.permission.PermissionResponse;
import com.example.demo.GDU.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    List<PermissionResponse> toPermissionResponseList(List<Permission> permissions);
    PermissionResponse toPermissionResponse(Permission permission);
}
