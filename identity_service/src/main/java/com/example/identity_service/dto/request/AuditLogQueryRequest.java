package com.example.identity_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Request filter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogQueryRequest {
    private LocalDateTime from;
    private LocalDateTime to;
    private String username;
    private String path;
}

