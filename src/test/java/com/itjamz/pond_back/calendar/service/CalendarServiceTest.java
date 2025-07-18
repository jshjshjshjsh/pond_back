package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import com.itjamz.pond_back.user.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private WorkHistoryRepository workHistoryRepository;

    @Test
    void saveWorkHistorySuccess() {
        // WorkHistoryDto workHistoryDto, Member member
        // given
        WorkHistoryDto workHistoryDto = WorkHistoryDto.builder()
                .startDate(LocalDateTime.of(2025, Month.APRIL, 5, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, Month.APRIL, 12, 0, 0, 0))
                .title("업체 미팅")
                .content("업체 미팅은 4월 5일이에요")
                .build();

        Member member = Member.builder()
                .sabun("123456")
                .id("tester")
                .name("test")
                .role(Member_Role.ROLE_LEADER)
                .build();

        //when
        WorkHistory workHistoryRegister = WorkHistory.builder()
                .startDate(workHistoryDto.getStartDate())
                .endDate(workHistoryDto.getEndDate())
                .title(workHistoryDto.getTitle())
                .content(workHistoryDto.getContent())
                .member(member)
                .build();

        Mockito.when(workHistoryRepository.save(Mockito.any(WorkHistory.class))).thenReturn(workHistoryRegister);
        WorkHistory savedWorkHistory = calendarService.saveWorkHistory(workHistoryDto, member);


        // then
        assertThat(savedWorkHistory.getStartDate()).isEqualTo(workHistoryDto.getStartDate());
        assertThat(savedWorkHistory.getEndDate()).isEqualTo(workHistoryDto.getEndDate());
        assertThat(savedWorkHistory.getTitle()).isEqualTo(workHistoryDto.getTitle());
        assertThat(savedWorkHistory.getContent()).isEqualTo(workHistoryDto.getContent());
        assertThat(savedWorkHistory.getMember().getSabun()).isEqualTo(member.getSabun()
        );
    }

    @Test
    void findWorkHistoryByDate() {
    }
}