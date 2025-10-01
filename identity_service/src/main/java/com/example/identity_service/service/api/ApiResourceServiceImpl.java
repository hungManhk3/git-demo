package com.example.identity_service.service.api;

import com.example.identity_service.dto.request.ApiResourceRequest;
import com.example.identity_service.dto.response.ApiResourceResponse;
import com.example.identity_service.entity.ApiResource;
import com.example.identity_service.mapper.ApiResourceMapper;
import com.example.identity_service.repository.ApiResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApiResourceServiceImpl implements ApiResourceService{
    private final ApiResourceRepository resourceRepository;
    private final ApiResourceMapper resourceMapper;
    @Override
    public ApiResourceResponse create(ApiResourceRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        ApiResource resource = resourceMapper.toApiResource(request);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setCreatedBy(name);
        return resourceMapper.toResponse(resourceRepository.save(resource));
    }
}
