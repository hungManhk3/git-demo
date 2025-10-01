package com.example.identity_service.service.role;

import com.example.identity_service.dto.request.PermissionAddRequest;
import com.example.identity_service.dto.request.RoleCreateRequest;
import com.example.identity_service.dto.request.RoleUpdateRequest;
import com.example.identity_service.dto.response.RolePerResponse;
import com.example.identity_service.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    RoleResponse roleCreate (RoleCreateRequest request);
    Page<RoleResponse> getRoles(String q, Pageable pageable);
    RolePerResponse getRole(String id);
    RoleResponse updateRole(RoleUpdateRequest request, String id);
    void deleteRole(String id, boolean force);
    RolePerResponse addPermission(PermissionAddRequest request, String id);
}
