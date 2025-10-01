package com.example.identity_service.controller;

import com.example.identity_service.dto.request.PermissionCreateRequest;
import com.example.identity_service.dto.request.PermissionUpdateRequest;
import com.example.identity_service.dto.response.ApiResponse;
import com.example.identity_service.dto.response.PermissionResponse;
import com.example.identity_service.service.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionCreateRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .content(permissionService.permissionCreate(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<PermissionResponse>> getPermissions(
            @RequestParam(required = false) String q,
            Pageable pageable
    ){
        Page<PermissionResponse> page = permissionService.getPermission(q, pageable);
        return ApiResponse.<List<PermissionResponse>>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<PermissionResponse> updatePermission(@PathVariable("id") String id,@RequestBody PermissionUpdateRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .content(permissionService.updatePermission(request,id))
                .build();
    }

    @PatchMapping("/{id}")
    ApiResponse<String> deletedPermission(@PathVariable("id") String id,@RequestParam(defaultValue = "false") boolean force){
        permissionService.deletePermission(id, force);
        return ApiResponse.<String>builder()
                .content("Permission has been deleted")
                .build();
    }
}
