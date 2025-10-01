package com.example.identity_service.entity;

import com.example.identity_service.enums.OverrideEffect;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_permission_override")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermissionOverride {
    @EmbeddedId
    private UserPermissionOverrideId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Column(length = 8, nullable = false)
    private OverrideEffect effect;

    private String note;
    private LocalDateTime createdAt;
    private String createdBy;

}
