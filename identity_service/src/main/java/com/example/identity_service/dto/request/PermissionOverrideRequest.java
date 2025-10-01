package com.example.identity_service.dto.request;

import com.example.identity_service.enums.OverrideEffect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionOverrideRequest {
    private String permissionId;
    private OverrideEffect effect;
    private String note;
}
