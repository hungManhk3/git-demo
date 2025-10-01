package com.example.identity_service.service.user;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.*;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.User;
import com.example.identity_service.entity.UserPermissionOverride;
import com.example.identity_service.entity.UserPermissionOverrideId;
import com.example.identity_service.enums.OverrideEffect;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.mapper.UserMapper;
import com.example.identity_service.mapper.UserPermissionOverrideMapper;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserPermissionOverrideRepository;
import com.example.identity_service.repository.UserRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final UserPermissionOverrideRepository userPermissionOverrideRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionOverrideMapper userPermissionOverrideMapper;
    @Override
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public UserResponse createUser(UserCreateRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw  new AppException(ErrorCode.USER_EXITED);
        }
        User user = userMapper.toUser(request);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(false);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserResponse> getUsers(String q, Short status, Pageable pageable) {
        return userRepository.searchUsers(q,status,pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    @PreAuthorize("hasAuthority('USER_READ')")
    public UserRoleResponse getUser(String id) {
        return userMapper.toUserRoleResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UserResponse updateUser(UserUpdateRequest request, String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // kiểm tra version
        if (!Objects.equals(user.getVersion(), request.getVersion())) {
            throw new OptimisticLockException(ErrorCode.VERSION_UPDATE);
        }
        userMapper.updateUser(user, request);
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(user.getUsername());

        return userMapper.toUserResponse(userRepository.save(user));    }

    @Override
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public void deletedUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserRoleResponse addRole(RoleAddRequest request, String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CODE_EXITED));
        user.getRoles().clear();
        var roles = roleRepository.findAllById(request.getRoleId());
//        user.setRoles(new HashSet<>(roles));
        user.getRoles().addAll(roles);
        return userMapper.toUserRoleResponse(userRepository.save(user));    }

    @Override
    public void changePassword(String id, PasswordRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPasswordHash(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserRoleResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXITED));

        return userMapper.toUserRoleResponse(user);    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserPermissionOverrideResponse> overridePermissionForUser(String userId, UserPermissionOverrideRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        List<UserPermissionOverride> listUserOverride = new ArrayList<>();
        for(PermissionOverrideRequest r: request.getOverrides()){
            Permission permission = permissionRepository.findById(r.getPermissionId())
                    .orElseThrow(()-> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
            UserPermissionOverride userPermissionOverride = UserPermissionOverride.builder()
                    .permission(permission)
                    .user(user)
                    .id(new UserPermissionOverrideId(userId,permission.getId()))
                    .effect(r.getEffect())
                    .note(r.getNote())
                    .createdAt(LocalDateTime.now())
                    .createdBy(currentUser)
                    .build();
            listUserOverride.add(userPermissionOverride);
        }
        return userPermissionOverrideMapper.toResponseList(userPermissionOverrideRepository.saveAll(listUserOverride));    }

    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteOverride(String userId, String permissionId) {
        UserPermissionOverrideId id = new UserPermissionOverrideId(userId, permissionId);
        if (!userPermissionOverrideRepository.existsById(id)) {
            throw new AppException(ErrorCode.OVERRIDE_NOT_FOUND);
        }
        userPermissionOverrideRepository.deleteById(id);
    }

    @Override
    public PermissionOverrideResponse listPermissionEffectiveUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Map<String,Boolean> effectiveMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission ->effectiveMap.put(permission.getCode(), true));
            });
        }

        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(user.getId());
        if (!CollectionUtils.isEmpty(overrides)) {
            overrides.forEach(o -> {
                Permission permission = o.getPermission();
                if (o.getEffect() == OverrideEffect.DENY) {
//                    effectiveMap.remove(permission.getCode());
                    effectiveMap.put(permission.getCode(), false);
                } else if (o.getEffect() == OverrideEffect.ALLOW) {
                    effectiveMap.put(permission.getCode(), true);
                }
            });
        }
        List<EffectivePermissionResponse> result = effectiveMap.entrySet().stream()
                .map(e -> new EffectivePermissionResponse(e.getKey(), e.getValue()))
                .toList();
        return new PermissionOverrideResponse(userId, result);
    }
    @Override
    public List<AuthCheckResponse> authCheckPermission(AuthCheckRequest request) {
        Map<String, Boolean> check = new HashMap<>();
        Map<String, String> reason = new HashMap<>();
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Permission permission = permissionRepository.findByCode(request.getPermissionCode()).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(user.getId());
        reason.put(permission.getCode(),"You don't have this right");
        check.put(permission.getCode(),false);
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(p -> {
                        if(p.getCode().equals(permission.getCode())){
                            check.put(p.getCode(),true);
                            reason.put(p.getCode(),"Ok");
                        }
                    });
            });
        }
        if (!CollectionUtils.isEmpty(overrides)){
            overrides.forEach(o -> {
                Permission p = o.getPermission();
                if(o.getEffect() == OverrideEffect.DENY && p.getCode().equals(permission.getCode())){
                    check.put(p.getCode(), false);
                    reason.put(p.getCode(),"Denied by user override");
                } else if (o.getEffect() == OverrideEffect.ALLOW && p.getCode().equals(permission.getCode())) {
                    check.put(p.getCode(), true);
                    reason.put(p.getCode(),"Ok");
                }
            });
        }
        log.warn(check.toString());
        log.warn(reason.toString());
        return check.entrySet().stream()
                .map(e -> AuthCheckResponse.builder()
                        .granted(e.getValue())
                        .reason(reason.getOrDefault(e.getKey(), ""))
                        .build())
                .toList();
    }
}
