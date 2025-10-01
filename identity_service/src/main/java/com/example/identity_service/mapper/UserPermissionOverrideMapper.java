package com.example.identity_service.mapper;

import com.example.identity_service.dto.response.UserPermissionOverrideResponse;
import com.example.identity_service.entity.UserPermissionOverride;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserPermissionOverrideMapper {
    UserPermissionOverrideResponse toResponse(UserPermissionOverride entity);
    List<UserPermissionOverrideResponse> toResponseList(List<UserPermissionOverride> entities);
}
