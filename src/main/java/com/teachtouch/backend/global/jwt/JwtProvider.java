package com.teachtouch.backend.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret; // Base64 권장(32바이트 이상)

    @Value("${jwt.expiration}")
    private long expirationMs; // access 토큰 만료(ms)

    private Key key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret이 비어 있습니다. application.yml을 확인하세요.");
        }
        // Base64 시크릿 우선 사용, 아니면 평문 사용
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            // Base64가 아니면 평문. HS256은 32바이트 이상 권장
            this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
    }

    public String generateAccessToken(String loginId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String loginId) {
        long now = System.currentTimeMillis();
        long refreshMs = 1000L * 60 * 60 * 24 * 14; // 필요하면 yml로 분리 가능
        return Jwts.builder()
                .setSubject(loginId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getLoginIdFromToken(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
