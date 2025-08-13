package com.itjamz.pond_back.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itjamz.pond_back.security.JwtUtil;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AiService aiService;
    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsService userDetailsService;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp(){
        Member member = Member.builder()
                .id("testuser")
                .sabun("12345")
                .name("test")
                .role(Member_Role.ROLE_NORMAL)
                .build();
        customUserDetails = new CustomUserDetails(member);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
    }


    @Test
    @DisplayName("ai summary 성공")
    void aiSummary() throws Exception {
        String requireString = "테스트용 요청 문자열이에요";

        mockMvc.perform(MockMvcRequestBuilders.post("/ai/summary")
                .contentType(MediaType.TEXT_PLAIN)
                .content(requireString)
                .with(csrf()))
            .andExpect(status().isOk());

        verify(aiService).getSummaryFromGemini(eq(requireString));
    }
}