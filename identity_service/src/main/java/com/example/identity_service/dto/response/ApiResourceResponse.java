package com.example.identity_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResourceResponse {
    private String id;
    private String resource;
    private String pathPattern;
    private String method;
    private String description;
    private LocalDateTime createdAt;
    private String createdBy;
}
