package com.example.identity_service.service.auth;

import com.example.identity_service.dto.request.AuthCheckRequest;
import com.example.identity_service.dto.request.AuthenticationRequest;
import com.example.identity_service.dto.request.IntrospectRequest;
import com.example.identity_service.dto.response.AuthCheckResponse;
import com.example.identity_service.dto.response.AuthenticationResponse;
import com.example.identity_service.dto.response.IntrospectResponse;
import com.example.identity_service.entity.Permission;
import com.example.identity_service.entity.User;
import com.example.identity_service.entity.UserPermissionOverride;
import com.example.identity_service.enums.OverrideEffect;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.PermissionRepository;
import com.example.identity_service.repository.UserPermissionOverrideRepository;
import com.example.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserRepository userRepository;
    private final UserPermissionOverrideRepository userPermissionOverrideRepository;
    private final PermissionRepository permissionRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SINGER_KEY;
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXITED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(6);
        boolean auth =  passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if (!auth){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
//        log.warn(token);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("manhht")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope" ,buildScope(user) )
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SINGER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        Set<String> scope = new HashSet<>();

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                scope.add("ROLE_" + role.getCode());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> scope.add(permission.getCode()));
            });
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(user.getId());
        if (!CollectionUtils.isEmpty(overrides)) {
            overrides.forEach(o -> {
                Permission permission = o.getPermission();
                if (o.getEffect() == OverrideEffect.DENY) {
                    scope.remove(permission.getCode());
                } else if (o.getEffect() == OverrideEffect.ALLOW) {
                    scope.add(permission.getCode());
                }
            });
        }
        return String.join(" ", scope);
    }
    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());

        SignedJWT signedJWT =   SignedJWT.parse(token);

        Date expTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .valid(verified && expTime.after(new Date()))
                .build();    }

    @Override
    public List<AuthCheckResponse> authCheckPermission(AuthCheckRequest request) {
        Map<String, Boolean> check = new HashMap<>();
        Map<String, String> reason = new HashMap<>();
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Permission permission = permissionRepository.findByCode(request.getPermissionCode()).orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(user.getId());
        reason.put(permission.getCode(),"You don't have this right");
        check.put(permission.getCode(),false);
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(p -> {
                        if(p.getCode().equals(permission.getCode())){
                            check.put(p.getCode(),true);
                            reason.put(p.getCode(),"Ok");
                        }
                    });
            });
        }
        if (!CollectionUtils.isEmpty(overrides)){
            overrides.forEach(o -> {
                Permission p = o.getPermission();
                if(o.getEffect() == OverrideEffect.DENY && p.getCode().equals(permission.getCode())){
                    check.put(p.getCode(), false);
                    reason.put(p.getCode(),"Denied by user override");
                } else if (o.getEffect() == OverrideEffect.ALLOW && p.getCode().equals(permission.getCode())) {
                    check.put(p.getCode(), true);
                    reason.put(p.getCode(),"Ok");
                }
            });
        }
        log.warn(check.toString());
        log.warn(reason.toString());
        return check.entrySet().stream()
                .map(e -> AuthCheckResponse.builder()
                        .granted(e.getValue())
                        .reason(reason.getOrDefault(e.getKey(), ""))
                        .build())
                .toList();
    }
}
