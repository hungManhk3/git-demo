package com.example.identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleResponse {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private Short status;
    private LocalDateTime createdAt;
    Set<RolePerResponse> roles;

}
