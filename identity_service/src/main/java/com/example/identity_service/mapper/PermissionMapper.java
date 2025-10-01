package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.PermissionCreateRequest;
import com.example.identity_service.dto.request.PermissionUpdateRequest;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Permission;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionCreateRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
    void updatePermission(@MappingTarget Permission permission, PermissionUpdateRequest request);

}
