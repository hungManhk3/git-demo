package com.example.identity_service.dto.response;

import com.example.identity_service.dto.request.PermissionOverrideRequest;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.UserPermissionOverrideId;
import com.example.identity_service.enums.OverrideEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermissionOverrideResponse {
    private UserPermissionOverrideId id;
    private Permission permission;
    private OverrideEffect effect;
    private String note;
    private LocalDateTime createdAt;
    private String createdBy;
}
