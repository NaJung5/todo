package com.najung.todo.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private Key key;

    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expire-ms}") long accessTokenValidity,
            @Value("${jwt.refresh-expire-ms}") long refreshTokenValidity) {
        this.secretKey = secretKey;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token 생성
    public String createToken(String userId, String role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role);
        claims.put("type", "access");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Claims 파싱
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // userId 가져오기
    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰 타입 검사 (access / refresh)
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getAccessTokenExpiryTime() {
        return accessTokenValidity;
    }
}
