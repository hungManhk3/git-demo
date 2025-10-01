package com.example.identity_service.repository;

import com.example.identity_service.entity.UserPermissionOverride;
import com.example.identity_service.entity.UserPermissionOverrideId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionOverrideRepository extends JpaRepository<UserPermissionOverride, UserPermissionOverrideId> {
    List<UserPermissionOverride> findByUserId(String userId);

}
