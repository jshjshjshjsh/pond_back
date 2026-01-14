package com.itjamz.pond_back.calendar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.dto.WorkSummaryDto;
import com.itjamz.pond_back.calendar.service.CalendarService;
import com.itjamz.pond_back.security.JwtUtil;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
// Import the csrf() method
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalendarController.class)
class CalendarControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CalendarService calendarService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private RedisTemplate<String, String> redisTemplate;

    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .id("testuser")
                .sabun("123456")
                .name("test")
                .role(MemberRole.ROLE_NORMAL)
                .build();
        customUserDetails = new CustomUserDetails(member);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities())
        );
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(customUserDetails);
    }

    @Test
    @DisplayName("업무 일지 저장 성공")
    void saveWorkHistory() throws Exception {
        WorkHistoryDto workHistoryDto = WorkHistoryDto.builder()
                .title("Test History")
                .content("Test Content")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(1))
                .isShare(true)
                .build();

        mockMvc.perform(post("/calendar/workhistory/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workHistoryDto))
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).saveWorkHistory(any(WorkHistoryDto.class), any(Member.class));
    }

    @Test
    @DisplayName("업무 일지 수정 성공")
    void updateWorkHistory() throws Exception {
        Long workHistoryId = 1L;
        WorkHistoryDto workHistoryDto = WorkHistoryDto.builder().title("Updated Title").build();

        mockMvc.perform(patch("/calendar/workhistory/{id}", workHistoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workHistoryDto))
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).updateWorkHistory(eq(workHistoryId), any(WorkHistoryDto.class), any(Member.class));
    }

    @Test
    @DisplayName("업무 일지 삭제 성공")
    void deleteWorkHistory() throws Exception {
        Long workHistoryId = 1L;

        mockMvc.perform(delete("/calendar/workhistory/{id}", workHistoryId)
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).deleteWorkHistory(eq(workHistoryId), any(Member.class));
    }

    @Test
    @DisplayName("업무 요약 저장 성공")
    void saveWorkSummary() throws Exception {
        WorkSummaryDto workSummaryDto = WorkSummaryDto.builder()
                .year(2025)
                .month(8)
                .summary("This is a summary.")
                .isShare(false)
                .build();

        mockMvc.perform(post("/calendar/worksummary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workSummaryDto))
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).saveWorkSummary(any(WorkSummaryDto.class), any(Member.class));
    }

    @Test
    @DisplayName("업무 요약 수정 성공")
    void updateWorkSummary() throws Exception {
        WorkSummaryDto workSummaryDto = WorkSummaryDto.builder()
                .id(1L)
                .isShare(true)
                .build();

        mockMvc.perform(patch("/calendar/worksummary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workSummaryDto))
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).updateWorkSummary(any(WorkSummaryDto.class), any(Member.class));
    }

    @Test
    @DisplayName("업무 요약 삭제 성공")
    void deleteWorkSummary() throws Exception {
        Long summaryId = 1L;

        mockMvc.perform(delete("/calendar/worksummary/{id}", summaryId)
                        .with(csrf())) // <-- Add CSRF token
                .andExpect(status().isOk());

        verify(calendarService).deleteWorkSummary(eq(summaryId), any(Member.class));
    }

    // GET requests do not need a CSRF token
    @Test
    @DisplayName("내 업무일지 기간별 조회 성공")
    void workHistoryList() throws Exception {
        when(calendarService.findWorkHistoryByDate(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/calendar/workhistory/list")
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-08-31"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("내 업무 요약 조회 성공")
    void getMyWorkSummary() throws Exception {
        when(calendarService.findWorkSummaryPersonal(any(int.class), any(int.class), any(Member.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/calendar/worksummary/list")
                        .param("year", "2025")
                        .param("month", "8"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("팀 업무 요약 조회 성공")
    void getTeamWorkSummary() throws Exception {
        when(calendarService.findTeamWorkSummary(any(int.class), any(int.class), any(Member.class)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/calendar/leader/worksummary/list")
                        .param("year", "2025")
                        .param("month", "8"))
                .andExpect(status().isOk());
    }
}