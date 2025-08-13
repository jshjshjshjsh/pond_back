package com.itjamz.pond_back.security.controller;

import com.itjamz.pond_back.AbstractContainerBaseTest;
import com.itjamz.pond_back.security.JwtUtil;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import com.itjamz.pond_back.user.repository.MemberRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // 각 테스트 실행 전에 실행되어 테스트 환경을 설정
    @BeforeEach
    void setUp() {
        // 이전 테스트의 영향을 없애기 위해 모든 사용자 정보를 삭제
        memberRepository.deleteAll();

        // 테스트에 사용할 사용자를 생성하고 저장합니다.
        Member testUser = Member.builder()
                .id("testuser")
                .sabun("12345")
                .name("test")
                .pw(passwordEncoder.encode("password")) // 비밀번호는 반드시 암호화하여 저장
                .role(Member_Role.ROLE_NORMAL)
                .build();
        memberRepository.save(testUser);
    }

    @Test
    @DisplayName("로그인 성공 - 200 OK 와 함께 AccessToken, RefreshToken 반환")
    void loginSuccess() throws Exception {
        // given: @BeforeEach에서 사용자 생성 완료

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // accessToken이 존재하는지
                .andExpect(cookie().exists("refreshToken"));   // refreshToken 쿠키가 존재하는지
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 또는 비밀번호 불일치 시 401 Unauthorized")
    void loginFail_withBadCredentials() throws Exception {
        // given: 존재하지 않는 사용자 정보

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"wronguser\", \"pw\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized()); // 401 상태 코드를 기대
    }

    @Test
    @DisplayName("로그아웃 성공 - 200 OK, 쿠키 및 Redis 토큰 삭제")
    void logoutSuccess() throws Exception {
        // given: 먼저 로그인을 성공하여 Access Token과 Refresh Token을 발급
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.accessToken");
        String redisKey = "RT:testuser"; // 실제 사용하는 Redis 키 형식

        // Redis에 Refresh Token이 저장되었는지 확인
        assertTrue(redisTemplate.hasKey(redisKey), "로그인 후 Redis에 Refresh Token이 있어야 합니다.");

        // when & then: 발급받은 Access Token으로 로그아웃을 요청
        mockMvc.perform(get("/logout")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("refreshToken", 0)); // 쿠키 만료시간이 0인지 확인

        // Redis에서 Refresh Token이 실제로 삭제되었는지 확인
        assertFalse(redisTemplate.hasKey(redisKey), "로그아웃 후 Redis에서 Refresh Token이 삭제되어야 합니다.");
    }

    @Test
    @DisplayName("Access Token 재발급 성공 - 유효한 리프레시 토큰")
    void reissueAccessToken_success() throws Exception {
        // given: 로그인하여 Refresh Token 쿠키를 발급
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshTokenCookie = loginResult.getResponse().getCookie("refreshToken");

        // when & then: 발급받은 쿠키로 재발급을 요청
        mockMvc.perform(post("/login/refresh")
                        .cookie(refreshTokenCookie)) // 요청에 쿠키를 포함
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty()); // 새로운 accessToken을 받았는지 확인
    }

    @Test
    @DisplayName("Access Token 재발급 실패 - 유효하지 않은 리프레시 토큰")
    void reissueAccessToken_fail_withInvalidToken() throws Exception {
        // given: 유효하지 않은 값으로 쿠키를 생성
        Cookie invalidCookie = new Cookie("refreshToken", "this-is-an-invalid-token");

        // when & then
        mockMvc.perform(post("/login/refresh")
                        .cookie(invalidCookie))
                .andExpect(status().isUnauthorized()); // 401 에러를 기대
    }
}