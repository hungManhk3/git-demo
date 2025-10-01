package com.example.identity_service.mapper;

import com.example.identity_service.dto.request.ApiResourceRequest;
import com.example.identity_service.dto.response.ApiResourceResponse;
import com.example.identity_service.entity.ApiResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApiResourceMapper {
    ApiResourceResponse toResponse (ApiResource apiResource);
    ApiResource toApiResource (ApiResourceRequest request);
}
