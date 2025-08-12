package com.itjamz.pond_back.security;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    // Mock HTTP 요청/응답 객체
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 SecurityContext를 항상 비움
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT 토큰으로 인증 성공")
    void doFilterInternal_Success_ValidToken() throws ServletException, IOException {
        // given
        final String token = "valid_jwt_token";
        final String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.singletonList(() -> "ROLE_NORMAL"));

        // Mock 객체들의 행동 정의
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(null); // Redis에 블랙리스트로 등록되어 있지 않음
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);

        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContextHolder에 인증 정보가 올바르게 설정되었는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
        // 필터 체인의 doFilter가 1번 호출되었는지 확인
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("로그아웃 처리된 (블랙리스트) 토큰으로 인증 실패")
    void doFilterInternal_Fail_BlacklistedToken() throws ServletException, IOException {
        // given
        final String token = "blacklisted_jwt_token";
        final String username = "testuser";

        // Mock 객체들의 행동 정의
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn("logout"); // Redis에 'logout'으로 값이 존재 (블랙리스트)

        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContextHolder에 인증 정보가 설정되지 않았는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        // userDetailsService.loadUserByUsername이 호출되지 않았는지 확인
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("JWT 토큰이 없는 요청은 인증 없이 통과")
    void doFilterInternal_Pass_NoToken() throws ServletException, IOException {
        // given
        // Authorization 헤더가 null인 상황
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // then
        // SecurityContextHolder에 인증 정보가 설정되지 않았는지 확인
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 인증 실패")
    void doFilterInternal_Fail_InvalidToken() throws ServletException, IOException {
        // given
        final String token = "invalid_jwt_token";
        final String username = "testuser";
        UserDetails userDetails = new User(username, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(token)).thenReturn(null);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(false); // 토큰이 유효하지 않음

        // when
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }
}