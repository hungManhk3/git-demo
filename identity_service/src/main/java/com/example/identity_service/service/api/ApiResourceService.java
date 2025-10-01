package com.example.identity_service.service.api;

import com.example.identity_service.dto.request.ApiResourceRequest;
import com.example.identity_service.dto.response.ApiResourceResponse;

public interface ApiResourceService {
    ApiResourceResponse create(ApiResourceRequest request);
}
