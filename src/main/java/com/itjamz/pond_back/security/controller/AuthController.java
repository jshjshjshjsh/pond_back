package com.itjamz.pond_back.security.controller;

import com.itjamz.pond_back.security.JwtUtil;
import com.itjamz.pond_back.security.service.UserDetailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailServiceImpl userDetailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final Environment env;

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("logout call");
        // 1. 헤더에서 Access Token 추출
        final String authorizationHeader = request.getHeader("Authorization");
        boolean isProduction = Arrays.asList(env.getActiveProfiles()).contains("prod");
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);
        }

        if (accessToken != null && !jwtUtil.isTokenExpired(accessToken)) {
            // 2. Access Token을 블랙리스트에 추가
            Long expiration = jwtUtil.getExpiration(accessToken);
            redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
        }

        // 3. 클라이언트의 Refresh Token 쿠키 삭제
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isProduction) // 👈 배포 환경(prod)일 때만 true로 설정
                .path("/")
                .maxAge(0) // 쿠키 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok("성공적으로 로그아웃되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getId(), authenticationRequest.getPw())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        final UserDetails userDetails = userDetailService.loadUserByUsername(authenticationRequest.getId());

        // 1. Access Token과 Refresh Token 생성
        final String accessToken = jwtUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 2. Refresh Token을 HttpOnly 쿠키에 담아 설정
        boolean isProduction = Arrays.asList(env.getActiveProfiles()).contains("prod");
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(isProduction) // 👈 배포 환경(prod)일 때만 true로 설정
                .path("/")
                .maxAge(7L * 24 * 60 * 60) // 7일
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


        // 3. Access Token은 JSON 바디로 전달
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
        return ResponseEntity.ok(responseBody);
    }

    // 4. Access Token 재발급을 위한 엔드포인트 추가
    @PostMapping("/login/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is missing.");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateAccessToken(userDetails);

                Map<String, String> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Refresh Token이 유효하지 않은 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
    }
}