package com.itjamz.pond_back.security.controller;

import com.itjamz.pond_back.security.service.UserDetailServiceImpl;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailServiceImpl userDetailService;

    @Test
    @DisplayName("로그인 성공 - 200 OK 와 함께 AcessToken 반환")
    void loginSuccess() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setId("testuser");
        request.setPw("password");

        // AuthenticationManager가 성공적으로 인증했다고 가정
        when(authenticationManager.authenticate(any())).thenReturn(null);
        // UserDetailService가 UserDetails를 반환한다고 가정
        when(userDetailService.loadUserByUsername("testuser"))
                .thenReturn(new User("testuser", "encoded_password", Collections.singletonList(new SimpleGrantedAuthority(Member_Role.ROLE_NORMAL.name()))));


        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"testuser\", \"pw\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // accessToken이 존재하는지
                .andExpect(cookie().exists("refreshToken")); // refreshToken 쿠키가 존재하는지
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 또는 비밀번호 불일치 시 401 Unauthorized")
    void login_fail_with_bad_credentials() throws Exception {
        // given
        // AuthenticationManager가 BadCredentialsException을 던진다고 가정
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("자격 증명 실패"));

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"wronguser\", \"pw\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized()); // 401 상태 코드를 기대
        }
}