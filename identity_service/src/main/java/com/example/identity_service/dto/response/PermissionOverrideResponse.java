package com.example.identity_service.dto.response;

import com.example.identity_service.enums.OverrideEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionOverrideResponse {
    private String userId;
    private List<EffectivePermissionResponse> effectivePermissions;
}
