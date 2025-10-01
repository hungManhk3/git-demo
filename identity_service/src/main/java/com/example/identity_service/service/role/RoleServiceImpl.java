package com.example.identity_service.service.role;

import com.example.identity_service.dto.request.PermissionAddRequest;
import com.example.identity_service.dto.request.RoleCreateRequest;
import com.example.identity_service.dto.request.RoleUpdateRequest;
import com.example.identity_service.dto.response.RolePerResponse;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.entity.Role;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.RoleMapper;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    @Override
    public RoleResponse roleCreate(RoleCreateRequest request) {
        if(roleRepository.existsByCode(request.getCode())){
            throw new AppException(ErrorCode.CODE_EXITED);
        }
        Role role = roleMapper.toRole(request);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        role.setDeleted(false);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public Page<RoleResponse> getRoles(String q, Pageable pageable) {
        return roleRepository.searchRoles(q, pageable)
                .map(roleMapper::toRoleResponse);    }

    @Override
    public RolePerResponse getRole(String id) {
        return roleMapper.toRolePerResponse(roleRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public RoleResponse updateRole(RoleUpdateRequest request, String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_EXITED));
        roleMapper.updateRole(role,request);
        role.setUpdatedAt(LocalDateTime.now());
        return roleMapper.toRoleResponse(roleRepository.save(role));    }

    @Override
    public void deleteRole(String id, boolean force) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
        boolean hasPermissions = !role.getPermissions().isEmpty();
        boolean hasUsers = userRepository.existsByRoles_Id(id);
//        log.warn(String.valueOf(hasUsers));
//        log.warn(String.valueOf(hasPermissions));
        if ((hasUsers || hasPermissions) && !force) {
            throw new AppException(ErrorCode.ROLE_IN_USE);
        }
        if (force) {
            userRepository.findAllByRoles_Id(id)
                    .forEach(user -> user.getRoles().remove(role));
            role.getPermissions().clear();
        }
        role.setDeleted(true);
        roleRepository.save(role);
    }

    @Override
    public RolePerResponse addPermission(PermissionAddRequest request, String id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_EXITED));
        var permissions = permissionRepository.findAllById(request.getPermissionId());
        role.setPermissions(new HashSet<>(permissions));
        return roleMapper.toRolePerResponse(roleRepository.save(role));    }
}
