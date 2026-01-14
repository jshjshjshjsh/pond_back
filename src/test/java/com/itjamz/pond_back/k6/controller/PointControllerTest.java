package com.itjamz.pond_back.k6.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itjamz.pond_back.k6.service.PointService;
import com.itjamz.pond_back.security.JwtRequestFilter;
import com.itjamz.pond_back.security.SecurityConfig;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PointController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtRequestFilter.class),
        }
)
@AutoConfigureMockMvc(addFilters = false)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointService pointService;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .id("testuser")
                .sabun("123456")
                .name("test")
                .role(MemberRole.ROLE_NORMAL)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
    }


    @Test
    @DisplayName("비관적락 포인트 입금 테스트")
    void depositPessimisticTest() throws Exception{

        mockMvc.perform(post("/k6/point/deposit/pessimistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(100L))
                )
                .andExpect(status().isOk());

        verify(pointService).depositPessimistic(any(String.class), any(Long.class));
    }


    @Test
    @DisplayName("낙관적락 포인트 입금 테스트")
    void depositOptimisticTest() throws Exception{

        mockMvc.perform(post("/k6/point/deposit/optimistic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(100L))
                )
                .andExpect(status().isOk());

        verify(pointService).depositOptimistic(any(String.class), any(Long.class));
    }


    @Test
    @DisplayName("포인트 입금 테스트")
    void depositTest() throws Exception{

        mockMvc.perform(post("/k6/point/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(100L))
                )
                .andExpect(status().isOk());

        verify(pointService).deposit(any(String.class), any(Long.class));
    }

    @Test
    @DisplayName("포인트 출금 테스트")
    void withdrawTest() throws Exception{

        mockMvc.perform(post("/k6/point/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(100L))
                )
                .andExpect(status().isOk());

        verify(pointService).withdraw(any(String.class), any(Long.class));
    }


    @Test
    @DisplayName("포인트 조회 테스트")
    void getPointTest() throws Exception{

        mockMvc.perform(get("/k6/point/point")
                )
                .andExpect(status().isOk());

        verify(pointService).getPoint(any(String.class));
    }
}