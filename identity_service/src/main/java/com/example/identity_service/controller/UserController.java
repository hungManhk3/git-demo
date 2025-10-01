package com.example.identity_service.controller;

import com.example.identity_service.dto.request.*;
import com.example.identity_service.dto.response.*;
import com.example.identity_service.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @PostMapping()
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .content(userService.createUser(request))
                .build();
    }
    // chua phan trang
//    @GetMapping()
//    ApiResponse<List<UserResponse>> getUsers( ){
//        return ApiResponse.<List<UserResponse>>builder()
//                .content(userService.getUsers())
//                .build();
//    }
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Short status,
            Pageable pageable) {
        Page<UserResponse> page = userService.getUsers(q,status,pageable);

        return ApiResponse.<List<UserResponse>>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }


    @GetMapping("/{id}")
    ApiResponse<UserRoleResponse> getUser(@PathVariable("id") String id){
        return ApiResponse.<UserRoleResponse>builder()
                .content(userService.getUser(id))
                .build();
    }
    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable("id") String id){
        return ApiResponse.<UserResponse>builder()
                .content(userService.updateUser(request, id))
                .build();
    }
    @PatchMapping("/{id}")
    ApiResponse<String> deleteUser(@PathVariable("id") String id){
        userService.deletedUser(id);
        return ApiResponse.<String>builder()
                .content("User has been deleted")
                .build();
    }

    @PostMapping("/{id}/roles:assign")
    ApiResponse<UserRoleResponse> addRole(@PathVariable("id") String id, @RequestBody RoleAddRequest request){
        return ApiResponse.<UserRoleResponse>builder()
                .content(userService.addRole(request,id))
                .build();
    }
    @PostMapping("/{id}/password")
    ApiResponse<String> changePassword(@PathVariable("id") String id, @RequestBody PasswordRequest request){
        userService.changePassword(id, request);
        return ApiResponse.<String>builder()
                .content("Password has been change")
                .build();
    }
    @GetMapping("/myInfo")
    ApiResponse<UserRoleResponse> getMyInfo(){
        return ApiResponse.<UserRoleResponse>builder()
                .content(userService.getMyInfo())
                .build();
    }

    @PostMapping("/{id}/permissions:override")
    public ApiResponse<List<UserPermissionOverrideResponse>> overridePermissions(
            @PathVariable("id") String userId,
            @RequestBody UserPermissionOverrideRequest request) {

        var overrides = userService.overridePermissionForUser(userId, request);

        return ApiResponse.<List<UserPermissionOverrideResponse>>builder()
                .code(200)
                .message("Overrides updated successfully")
                .content(overrides)
                .build();
    }
    @DeleteMapping("/{userId}/{permissionId}")
    public ApiResponse<String> deleteOverride(@PathVariable("userId") String userId, @PathVariable("permissionId") String permissionId){
        userService.deleteOverride(userId,permissionId);
        return ApiResponse.<String>builder()
                .content("Deleted ok")
                .build();
    }

    @GetMapping("/{id}/permissions:effective")
    public ApiResponse<PermissionOverrideResponse> getListPerUserByUserId(@PathVariable("id") String id){
        return ApiResponse.<PermissionOverrideResponse>builder()
                .content(userService.listPermissionEffectiveUser(id))
                .build();
    }
}
