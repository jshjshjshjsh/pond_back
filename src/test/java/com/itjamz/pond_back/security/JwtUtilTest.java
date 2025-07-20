package com.itjamz.pond_back.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp(){
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "this-is-a-super-secret-key-for-jwt-util-testing-12345");
    }

    @Test
    @DisplayName("JWT 토큰 생성 및 사용자 이름 추출 성공")
    void generateAndExtractToken(){
        // given
        UserDetails userDetails = new User("tester", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_NORMAL")));

        // when
        String token = jwtUtil.generateAccessToken(userDetails);
        String username = jwtUtil.extractUsername(token);

        assertThat(token).isNotNull();
        assertThat(username).isEqualTo("tester");
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공")
    void validateTokenSuccess(){
        // given
        UserDetails userDetails = new User("tester", "password", Collections.emptyList());
        String token = jwtUtil.generateAccessToken(userDetails);

        // when
        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패")
    void validateTokenExpiredFails() throws InterruptedException {
        // given
        // 테스트를 위해 만료 시간을 0으로 설정하여 즉시 만료되는 토큰 생성
        UserDetails userDetails = new User("tester", "password", Collections.emptyList());
        String expiredToken = jwtUtil.generateTokenForTest(userDetails.getUsername(), -1L);

        // when & then
        // 토큰 만료 시간이 매우 짧으므로, 약간의 시간차를 두고 검증
        Thread.sleep(10);
        assertThatThrownBy(() -> jwtUtil.validateToken(expiredToken, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }
}