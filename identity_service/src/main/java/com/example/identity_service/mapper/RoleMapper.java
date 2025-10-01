package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.RoleCreateRequest;
import com.example.identity_service.dto.request.RoleUpdateRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.dto.response.RolePerResponse;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleCreateRequest request);
    RoleResponse toRoleResponse(Role role);
    RolePerResponse toRolePerResponse(Role role);

    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);

}
