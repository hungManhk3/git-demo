package com.example.identity_service.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;
import java.util.Date;

public class TestJwtUtil {
    public static String generateExpiredToken(String username, String signerKey) throws Exception {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("manhht")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().minusSeconds(60))) // đã hết hạn
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new MACSigner(signerKey.getBytes()));
        return jwt.serialize();
    }
}

