package com.example.demo.GDU.mappers;

import com.example.demo.GDU.dto.role.RoleResponse;
import com.example.demo.GDU.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
    RoleResponse roleToRoleResponse(Role role);
    List<RoleResponse> rolesToRoleResponseList(List<Role> roles);

}
