package com.example.identity_service.service.user;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse createUser (UserCreateRequest request);
    Page<UserResponse> getUsers(String q, Short status, Pageable pageable);
    UserRoleResponse getUser(String id);
    UserResponse updateUser(UserUpdateRequest request, String id);
    void deletedUser(String id);
    UserRoleResponse addRole(RoleAddRequest request, String id);
    void changePassword(String id, PasswordRequest request);
    UserRoleResponse getMyInfo();
    List<UserPermissionOverrideResponse> overridePermissionForUser(String userId, UserPermissionOverrideRequest request);
    void deleteOverride(String userId, String permissionId);
    PermissionOverrideResponse listPermissionEffectiveUser(String userId);
    List<AuthCheckResponse> authCheckPermission(AuthCheckRequest request);

}
