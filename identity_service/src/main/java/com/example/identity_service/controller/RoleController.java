package com.example.identity_service.controller;

import com.example.identity_service.dto.request.PermissionAddRequest;
import com.example.identity_service.dto.request.RoleCreateRequest;
import com.example.identity_service.dto.request.RoleUpdateRequest;
import com.example.identity_service.dto.response.ApiResponse;
import com.example.identity_service.dto.response.RolePerResponse;
import com.example.identity_service.dto.response.RoleResponse;
import com.example.identity_service.service.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/roles")
public class RoleController {
    private final RoleService roleService;

    @PostMapping()
    ApiResponse<RoleResponse> createRole(@RequestBody RoleCreateRequest request){
        return ApiResponse.<RoleResponse>builder()
                .content(roleService.roleCreate(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<RoleResponse>> getRoles(
            @RequestParam(required = false) String q,
            Pageable pageable
    ){
        Page<RoleResponse> page = roleService.getRoles(q, pageable);

        return ApiResponse.<List<RoleResponse>>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<RoleResponse> updateRole(@PathVariable("id") String id,@RequestBody RoleUpdateRequest request){
        return ApiResponse.<RoleResponse>builder()
                .content(roleService.updateRole(request,id))
                .build();
    }

    @PatchMapping("/{id}")
    ApiResponse<String> deletedRole(@PathVariable("id") String id, @RequestParam(defaultValue = "false") boolean force){
        roleService.deleteRole(id, force);
        return ApiResponse.<String>builder()
                .content("Role has been deleted")
                .build();
    }
    @GetMapping("/{id}")
    ApiResponse<RolePerResponse> getRole(@PathVariable("id") String id){
        return ApiResponse.<RolePerResponse>builder()
                .content(roleService.getRole(id))
                .build();
    }

    @PostMapping("/{id}/permissions:assign")
    ApiResponse<RolePerResponse> addPermission(@RequestBody PermissionAddRequest request, @PathVariable("id") String id){
        return ApiResponse.<RolePerResponse>builder()
                .content(roleService.addPermission(request, id))
                .build();
    }
}
