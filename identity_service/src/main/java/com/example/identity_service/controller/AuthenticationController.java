package com.example.identity_service.controller;

import com.example.identity_service.dto.request.AuthCheckRequest;
import com.example.identity_service.dto.request.AuthenticationRequest;
import com.example.identity_service.dto.request.IntrospectRequest;
import com.example.identity_service.dto.response.ApiResponse;
import com.example.identity_service.dto.response.AuthCheckResponse;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.service.auth.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result = service.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .content(result)
                .build();
    }
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = service.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .content(result)
                .build();
    }

    @PostMapping("/check")
    ApiResponse<List<AuthCheckResponse>> authCheckPermissionInUserById(@RequestBody AuthCheckRequest request){
        return ApiResponse.<List<AuthCheckResponse>>builder()
                .content(service.authCheckPermission(request))
                .build();
    }



}
