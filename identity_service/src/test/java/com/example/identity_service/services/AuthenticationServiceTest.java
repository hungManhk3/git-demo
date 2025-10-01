package com.example.identity_service.services;

import com.example.identity_service.dto.request.AuthenticationRequest;
import com.example.identity_service.dto.request.IntrospectRequest;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.entity.UserPermissionOverride;
import com.example.identity_service.enums.OverrideEffect;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserPermissionOverrideRepository;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.auth.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/test.properties")
class AuthenticationServiceTest {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired UserRepository userRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired PermissionRepository permissionRepository;
    @Autowired UserPermissionOverrideRepository overrideRepository;

    User user;

    @BeforeEach
    void setup() {
        overrideRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();

        // tạo permission & role
        Permission p1 = permissionRepository.save(Permission.builder()
                .code("USER_READ")
                .name("Xem user")
                .build());

        Role r1 = roleRepository.save(Role.builder()
                .code("ADMIN")
                .name("Admin role")
                .permissions(Set.of(p1))
                .build());

        user = userRepository.save(User.builder()
                .username("manh")
                        .status((short) 1)
                .passwordHash(new BCryptPasswordEncoder(6).encode("123456"))
                .roles(Set.of(r1))
                .build());
    }

    @Test
    void authenticate_success() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("manh")
                .password("123456")
                .build();

        AuthenticationResponse res = authenticationService.authenticate(req);
        assertTrue(res.isAuthenticated());
        assertNotNull(res.getToken());
    }

    @Test
    void authenticate_wrongPassword_throwException() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("manh")
                .password("wrong")
                .build();

        assertThrows(AppException.class, () -> authenticationService.authenticate(req));
    }

    @Test
    void authenticate_userNotFound_throwException() {
        AuthenticationRequest req = AuthenticationRequest.builder()
                .username("ghost")
                .password("123")
                .build();

        assertThrows(AppException.class, () -> authenticationService.authenticate(req));
    }

    @Test
    void introspect_validToken_ok() throws Exception {
        var token = authenticationService.authenticate(
                AuthenticationRequest.builder()
                        .username("manh").password("123456").build()
        ).getToken();

        IntrospectResponse res = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build()
        );

        assertTrue(res.isValid());
    }

    @Test
    void introspect_expiredToken_invalid() throws Exception {
        // tạo token với expiry trong quá khứ
        var token = TestJwtUtil.generateExpiredToken("manh", "12345678901234567890123456789012");

        IntrospectResponse res = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build()
        );

        assertFalse(res.isValid());
    }

    @Test
    void override_denyPermission() {
        // thêm override DENY
        Permission p1 = permissionRepository.findAll().get(0);
        overrideRepository.save(UserPermissionOverride.builder()
                .user(user)
                .permission(p1)
                .effect(OverrideEffect.DENY)
                .build());

        AuthenticationResponse res = authenticationService.authenticate(
                AuthenticationRequest.builder()
                        .username("manh").password("123456").build()
        );

        assertFalse(res.getToken().contains("USER_READ"));
    }

    @Test
    void override_allowExtraPermission() {
        Permission p2 = permissionRepository.save(Permission.builder()
                .id(UUID.randomUUID().toString())
                .code("USER_CREATE")
                .name("Tạo user")
                .build());

        overrideRepository.save(UserPermissionOverride.builder()
                .user(user)
                .permission(p2)
                .effect(OverrideEffect.ALLOW)
                .build());

        AuthenticationResponse res = authenticationService.authenticate(
                AuthenticationRequest.builder()
                        .username("manh").password("123456").build()
        );

        assertTrue(res.getToken().contains("USER_CREATE"));
    }
}

