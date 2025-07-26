package com.itjamz.pond_back.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Collection; // Collection을 import 해야 합니다.

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7; // 7 days

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Long getExpiration(String token) {
        Date expiration = extractExpiration(token);
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    // --- [수정] Access Token 생성 메소드 ---
    public String generateAccessToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), userDetails.getAuthorities(), ACCESS_TOKEN_EXPIRE_TIME);
    }

    // --- [수정] Refresh Token 생성 메소드 ---
    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(userDetails.getUsername(), userDetails.getAuthorities(), REFRESH_TOKEN_EXPIRE_TIME);
    }

    // --- [수정] 역할 정보를 포함하도록 수정된 createToken 메소드 (기존 메소드는 삭제) ---
    private String createToken(String subject, Collection<? extends GrantedAuthority> authorities, long expireTime) {
        long nowMillis = System.currentTimeMillis();

        String authString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(subject)
                .claim("auth", authString) // "auth" 클레임 추가
                .issuedAt(new Date(nowMillis))
                .expiration(new Date(nowMillis + expireTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 테스트에서만 사용할 메서드
    public String generateTokenForTest(String subject, long expireTime) {
        // 테스트용 메소드도 역할 정보를 포함하도록 수정 (필요 시 빈 권한 전달)
        return createToken(subject, java.util.Collections.emptyList(), expireTime);
    }
}