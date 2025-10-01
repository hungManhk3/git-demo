package com.example.identity_service.entity;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_log",
        indexes = {
                @Index(name = "idx_occurred_at", columnList = "occurred_at"),
                @Index(name = "idx_username", columnList = "username"),
                @Index(name = "idx_path", columnList = "path")
        })
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDateTime occurredAt;
    private String username;
    private String method;
    private String path;
    private Integer status;
    private String ip;
    private String userAgent;
    private String action;
    private String requestId;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payloadSnapshot;

}
