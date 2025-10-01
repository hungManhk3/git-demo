package com.example.identity_service.service.permission;

import com.example.identity_service.dto.request.PermissionCreateRequest;
import com.example.identity_service.dto.request.PermissionUpdateRequest;
import com.example.identity_service.dto.response.PermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    PermissionResponse permissionCreate (PermissionCreateRequest request);
    Page<PermissionResponse> getPermission(String q, Pageable pageable);
    PermissionResponse updatePermission(PermissionUpdateRequest request, String id);
    void deletePermission(String id, boolean force);
}
