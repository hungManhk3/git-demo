package com.example.identity_service.service.log;

import com.example.identity_service.dto.response.AuditLogResponse;
import com.example.identity_service.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {
    Page<AuditLogResponse> search(
            LocalDateTime from,
            LocalDateTime to,
            String username,
            String path,
            Integer status,
            Pageable pageable
    );
    void save(AuditLog log);
}
