package com.example.identity_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "api_resource")
public class ApiResource {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String resource;
    private String pathPattern;
    private String method;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean deleted = false;
}
