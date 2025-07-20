package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private WorkHistoryRepository workHistoryRepository;


    @Test
    @DisplayName("WorkHistory 팀 포함 저장 시나리오")
    void workHistorySaveWithTeam(){

        // WorkHistoryDto workHistoryDto, Member member
        // given
        WorkHistoryDto workHistoryDto = WorkHistoryDto.builder()
                .startDate(LocalDateTime.of(2025, Month.APRIL, 5, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, Month.APRIL, 12, 0, 0, 0))
                .title("업체 미팅")
                .content("업체 미팅은 4월 5일이에요")
                .team(TeamDto.builder().id(1L).build())
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
                .team(Team.builder().id(1L).teamName("TEAM1").build())
                .build();

        Mockito.when(workHistoryRepository.save(Mockito.any(WorkHistory.class))).thenReturn(workHistoryRegister);
        WorkHistory savedWorkHistory = calendarService.saveWorkHistory(workHistoryDto, member);


        // then
        assertThat(savedWorkHistory.getStartDate()).isEqualTo(workHistoryDto.getStartDate());
        assertThat(savedWorkHistory.getEndDate()).isEqualTo(workHistoryDto.getEndDate());
        assertThat(savedWorkHistory.getTitle()).isEqualTo(workHistoryDto.getTitle());
        assertThat(savedWorkHistory.getContent()).isEqualTo(workHistoryDto.getContent());
        assertThat(savedWorkHistory.getMember().getSabun()).isEqualTo(member.getSabun());
        assertThat(savedWorkHistory.getTeam().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("WorkHistory 팀 없이 저장 시나리오")
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
        assertThat(savedWorkHistory.getMember().getSabun()).isEqualTo(member.getSabun());
    }

    @Test
    @DisplayName("WorkHistory 날짜로 조회")
    void findWorkHistoryByDate() {
        // LocalDate startDate, LocalDate endDate, Member member
        // given
        LocalDate startDate = LocalDate.of(2025, Month.APRIL, 5);
        LocalDate endDate = LocalDate.of(2025, Month.APRIL, 23);

        Member member = Member.builder()
                .sabun("123456")
                .id("tester")
                .name("test")
                .role(Member_Role.ROLE_LEADER)
                .build();

        Team team1 = Team.builder()
                .teamName("TEAM1")
                .build();

        List<WorkHistory> workHistory = new ArrayList<>();
        List<WorkHistoryDto> workHistoryDto = new ArrayList<>();

        WorkHistory test1 = WorkHistory.builder()
                .startDate(LocalDateTime.of(2025, Month.APRIL, 3, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, Month.APRIL, 8, 0, 0, 0))
                .title("test1")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test2 = WorkHistory.builder()
                .startDate(LocalDateTime.of(2025, Month.APRIL, 10, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, Month.APRIL, 19, 0, 0, 0))
                .title("test2")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test3 = WorkHistory.builder()
                .startDate(LocalDateTime.of(2025, Month.APRIL, 20, 0, 0, 0))
                .endDate(LocalDateTime.of(2025, Month.MAY, 2, 0, 0, 0))
                .title("test3")
                .member(member)
                .team(team1)
                .build();


        workHistory.add(test1);
        workHistory.add(test2);
        workHistory.add(test3);

        workHistoryDto.add(WorkHistoryDto.from(test1));
        workHistoryDto.add(WorkHistoryDto.from(test2));
        workHistoryDto.add(WorkHistoryDto.from(test3));

        Mockito.when(workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay(), member.getSabun())).thenReturn(workHistory);


        // when
        List<WorkHistoryDto> findWorkHistories = calendarService.findWorkHistoryByDate(startDate, endDate, member);

        assertThat(findWorkHistories.get(0).getTitle()).isEqualTo(workHistoryDto.get(0).getTitle());
        assertThat(findWorkHistories.get(1).getTitle()).isEqualTo(workHistoryDto.get(1).getTitle());
        assertThat(findWorkHistories.get(2).getTitle()).isEqualTo(workHistoryDto.get(2).getTitle());
    }
}