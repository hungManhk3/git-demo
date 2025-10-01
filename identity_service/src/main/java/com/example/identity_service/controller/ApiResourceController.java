package com.example.identity_service.controller;

import com.example.identity_service.dto.request.ApiResourceRequest;
import com.example.identity_service.dto.response.ApiResourceResponse;
import com.example.identity_service.dto.response.ApiResponse;
import com.example.identity_service.service.api.ApiResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
public class ApiResourceController {
    private final ApiResourceService service;
    @PostMapping()
    ApiResponse<ApiResourceResponse> create(@RequestBody ApiResourceRequest request){
        return ApiResponse.<ApiResourceResponse>builder()
                .content(service.create(request))
                .build();
    }
}
