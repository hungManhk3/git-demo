package com.example.identity_service.config;

import com.example.identity_service.entity.AuditLog;
import com.example.identity_service.service.log.AuditLogService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class AuditLoggingFilter implements Filter {


    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        chain.doFilter(wrappedRequest, response);

//        ContentCachingRequestWrapper wrappedRequest =
//                new ContentCachingRequestWrapper((HttpServletRequest) request);

//        HttpServletResponse res = (HttpServletResponse) response;

//        String name = wrappedRequest.getRemoteUser() != null ? wrappedRequest.getRemoteUser() : "anonymous";
//        String requestId = UUID.randomUUID().toString();
//        try {
//            chain.doFilter(wrappedRequest, response);
//        } finally {
//            JsonNode payload = null;
//            try {
//                byte[] buf = wrappedRequest.getContentAsByteArray();
//                log.warn(String.valueOf(buf.length));
//                if (buf.length > 0) {
//                    String body = new String(buf, StandardCharsets.UTF_8);
//                    payload = objectMapper.readTree(body);
//                }
//            } catch (Exception e) {
//            }
//            AuditLog log = AuditLog.builder()
//                    .username(name)
//                    .method(wrappedRequest.getMethod())
//                    .path(wrappedRequest.getRequestURI())
//                    .status(res.getStatus())
//                    .ip(wrappedRequest.getRemoteAddr())
//                    .userAgent(wrappedRequest.getHeader("User-Agent"))
//                    .requestId(requestId)
//                    .payloadSnapshot(payload != null ? payload.toString() : null)
//                    .build();
//
//            auditLogService.save(log);
//        }
    }
}



