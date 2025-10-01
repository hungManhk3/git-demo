package com.example.identity_service.config;

import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.Role;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.RoleRepository;
import com.example.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder encoder;
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";
    @NonFinal
    static final String ADMIN_CODE = "ADMIN";
    @NonFinal
    static final String ADMIN_EMAIL = "admin@gmail.com";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository){
        log.info("Initializing application.....");
        return args -> {
            var roles = new HashSet<Role>();
            if (roleRepository.findByCode(ADMIN_CODE).isEmpty()){
                var pers = permissionRepository.findAll();
                Role adminRole = roleRepository.save(Role.builder()
                        .code(ADMIN_CODE)
                        .name("admin")
                        .description("Toàn quyền")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .deleted(false)
                        .permissions(new HashSet<>(pers))
                        .build());
                roles.add(adminRole);
            }
            Role adminRole = roleRepository.findByCode(ADMIN_CODE).orElseThrow(()-> new AppException(ErrorCode.CODE_EXITED));
            roles.add(adminRole);
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                userRepository.save(User.builder()
                                .roles(roles)
                                .status((short) 1)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .username(ADMIN_USER_NAME)
                                .passwordHash(encoder.encode(ADMIN_PASSWORD))
                                .email(ADMIN_EMAIL)
                                .deleted(false)
                        .build());
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }

}
