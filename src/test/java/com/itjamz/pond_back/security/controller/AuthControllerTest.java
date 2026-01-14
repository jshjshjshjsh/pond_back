package com.itjamz.pond_back.security.controller;

import com.itjamz.pond_back.global.config.TestRedisConfig; // <--- 1. 임포트 확인
import com.itjamz.pond_back.security.JwtUtil;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberPw;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import com.itjamz.pond_back.user.infra.repository.MemberJpaRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean; // <--- 추가
import org.springframework.context.annotation.Import; // <--- 추가
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations; // <--- 추가
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;          // <--- 추가
import static org.mockito.BDDMockito.given;              // <--- 추가
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Import(TestRedisConfig.class) // <--- 2. 핵심: 아까 만든 설정 파일 로드
class AuthControllerTest { // extends 제거된 것 유지

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberJpaRepository memberRepository;

    // RedisTemplate은 실제 Bean 대신 MockBean으로 대체하여 동작 제어
    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private ValueOperations<String, String> valueOperations; // redisTemplate.opsForValue()를 위해 필요

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();

        Member testUser = Member.builder()
                .id("testuser")
                .sabun("12345")
                .name("test")
                .pw(MemberPw.create("password", passwordEncoder))
                .role(MemberRole.ROLE_NORMAL)
                .build();
        memberRepository.save(testUser);

        // --- 3. Redis 동작 Mocking (가짜로 동작하게 설정) ---
        // redisTemplate.opsForValue() 호출 시 mock 객체 반환
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // hasKey 호출 시 무조건 true 반환 (테스트 통과용, 필요시 false로 조정)
        given(redisTemplate.hasKey(any())).willReturn(true);

        // delete 호출 시 아무것도 안 함
        given(redisTemplate.delete(any(String.class))).willReturn(true);
    }

    @Test
    @DisplayName("로그인 성공 - 200 OK 와 함께 AccessToken, RefreshToken 반환")
    void loginSuccess() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 또는 비밀번호 불일치 시 401 Unauthorized")
    void loginFail_withBadCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"wronguser\", \"pw\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() throws Exception {
        // given
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.accessToken");

        // when & then
        mockMvc.perform(get("/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    @Test
    @DisplayName("Access Token 재발급 성공")
    void reissueAccessToken_success() throws Exception {
        // given
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refreshToken");

        // when & then
        mockMvc.perform(post("/login/refresh")
                        .cookie(refreshTokenCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Access Token 재발급 실패 - 유효하지 않은 리프레시 토큰")
    void reissueAccessToken_fail_withInvalidToken() throws Exception {
        Cookie invalidCookie = new Cookie("refreshToken", "this-is-an-invalid-token");

        mockMvc.perform(post("/login/refresh")
                        .cookie(invalidCookie))
                .andExpect(status().isUnauthorized());
    }
}