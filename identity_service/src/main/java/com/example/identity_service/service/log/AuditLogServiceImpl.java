package com.example.identity_service.service.log;

import com.example.identity_service.dto.response.AuditLogResponse;
import com.example.identity_service.entity.AuditLog;
import com.example.identity_service.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService{
    private final AuditLogRepository auditLogRepository;

    @Override
    public Page<AuditLogResponse> search(LocalDateTime from, LocalDateTime to, String username, String path, Integer status, Pageable pageable) {
        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("occurredAt"), from));
            }
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("occurredAt"), to));
            }
            if (username != null) {
                predicates.add(cb.equal(root.get("username"), username));
            }
            if (path != null) {
                predicates.add(cb.like(root.get("path"), "%" + path + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return auditLogRepository.findAll(spec, pageable)
                .map(log -> AuditLogResponse.builder()
                        .id(log.getId())
                        .username(log.getUsername())
                        .method(log.getMethod())
                        .path(log.getPath())
                        .status(log.getStatus())
                        .occurredAt(log.getOccurredAt())
                        .ip(log.getIp())
                        .userAgent(log.getUserAgent())
                        .build()
                );    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AuditLog log) {
        if (log.getOccurredAt() == null) {
            log.setOccurredAt(LocalDateTime.now());
        }
        auditLogRepository.save(log);
    }
}
