package com.example.identity_service.service.permission;

import com.example.identity_service.dto.request.PermissionCreateRequest;
import com.example.identity_service.dto.request.PermissionUpdateRequest;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.Role;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.PermissionMapper;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService{
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;
    @Override
    public PermissionResponse permissionCreate(PermissionCreateRequest request) {
        if(permissionRepository.existsByCode(request.getCode())){
            throw new AppException(ErrorCode.CODE_EXITED);
        }
        Role adminRole = roleRepository.findByCode("ADMIN").orElseThrow(() -> new AppException(ErrorCode.CODE_EXITED));
        Permission permission = permissionMapper.toPermission(request);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        permission.setDeleted(false);
        Permission per = permissionRepository.save(permission);
        adminRole.getPermissions().add(per);
        roleRepository.save(adminRole);
        return permissionMapper.toPermissionResponse(per);    }

    @Override
    public Page<PermissionResponse> getPermission(String q, Pageable pageable) {
        return permissionRepository.searchPermissions(q, pageable)
                .map(permissionMapper::toPermissionResponse);    }

    @Override
    public PermissionResponse updatePermission(PermissionUpdateRequest request, String id) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_EXITED));
        permissionMapper.updatePermission(permission,request);
        permission.setUpdatedAt(LocalDateTime.now());
        return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
    }

    @Override
    public void deletePermission(String id, boolean force) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        boolean hasRole = roleRepository.existsByPermissions_Id(id);
        if(hasRole && !force){
            throw new AppException(ErrorCode.PERMISSION_IN_USE);
        }
        if(force){
            roleRepository.findAllByPermissions_Id(id).forEach(role -> role.getPermissions().remove(permission));
        }
        permission.setDeleted(true);
        permissionRepository.save(permission);
    }
}
