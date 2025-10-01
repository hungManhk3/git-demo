package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.UserCreateRequest;
import com.example.identity_service.dto.request.UserUpdateRequest;
import com.example.identity_service.dto.response.UserResponse;
import com.example.identity_service.dto.response.UserRoleResponse;
import com.example.identity_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "password", target = "passwordHash")
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);
    UserRoleResponse toUserRoleResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
