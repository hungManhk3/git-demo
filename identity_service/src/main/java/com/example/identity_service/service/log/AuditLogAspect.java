package com.example.identity_service.service.log;

import com.example.identity_service.entity.AuditLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Aspect
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {
    private final AuditLogService auditService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final ObjectMapper objectMapper;

    @Around("execution(* com.example.identity_service.controller..*(..))")
    public Object logSave(ProceedingJoinPoint joinPoint) throws Throwable {

        String name = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        String requestId = UUID.randomUUID().toString();

        String payloadSnapshot = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (!(arg instanceof ServletRequest) && !(arg instanceof ServletResponse)) {
//                log.info("Payload snapshot: {}", objectMapper.writeValueAsString(arg));
                payloadSnapshot = objectMapper.writeValueAsString(arg);
            }
        }
        Object result = joinPoint.proceed();

        AuditLog logEntry = AuditLog.builder()
                .username(name)
                .method(request.getMethod())
                .path(request.getRequestURI())
                .status(response.getStatus())
                .ip(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .requestId(requestId)
                .action(joinPoint.getSignature().toShortString())
                .payloadSnapshot(payloadSnapshot)
                .build();
        auditService.save(logEntry);

        return result;
    }

}

