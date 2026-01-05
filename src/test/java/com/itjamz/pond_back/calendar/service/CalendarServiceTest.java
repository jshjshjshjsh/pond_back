package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.dto.WorkSummaryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.domain.entity.WorkRecordDate;
import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.calendar.repository.WorkSummaryRepository;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    private CalendarService calendarService;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private WorkHistoryRepository workHistoryRepository;
    @Mock
    private WorkSummaryRepository workSummaryRepository;


    @Test
    @DisplayName("WorkHistory 수정 성공")
    void updateWorkHistorySuccess() {
        // given
        Long workHistoryId = 1L;
        Member member = Member.builder().id("tester").sabun("123456").build();
        WorkHistoryDto updateDto = WorkHistoryDto.builder().title("수정된 제목").content("수정된 내용").build();
        WorkHistory existingWorkHistory = WorkHistory.builder().id(workHistoryId).member(member).title("원본 제목").build();

        when(workHistoryRepository.findById(workHistoryId)).thenReturn(Optional.of(existingWorkHistory));

        // when
        calendarService.updateWorkHistory(workHistoryId, updateDto, member);

        // then
        assertThat(existingWorkHistory.getTitle()).isEqualTo("수정된 제목");
        assertThat(existingWorkHistory.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("WorkHistory 수정 실패 - 존재하지 않는 ID")
    void updateWorkHistoryFail_NotFound() {
        // given
        Long workHistoryId = 99L;
        Member member = Member.builder().id("tester").sabun("123456").build();
        WorkHistoryDto updateDto = WorkHistoryDto.builder().title("수정된 제목").build();

        when(workHistoryRepository.findById(workHistoryId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> calendarService.updateWorkHistory(workHistoryId, updateDto, member))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[workhistory 조회 실패]");
    }

    @Test
    @DisplayName("WorkHistory 삭제 성공")
    void deleteWorkHistorySuccess() {
        // given
        Long workHistoryId = 1L;
        Member member = Member.builder().id("tester").sabun("123456").build();
        WorkHistory existingWorkHistory = WorkHistory.builder().id(workHistoryId).member(member).build();

        when(workHistoryRepository.findById(workHistoryId)).thenReturn(Optional.of(existingWorkHistory));
        // delete 메서드는 반환값이 없으므로, 호출되는 것만 검증하면 됨
        doNothing().when(workHistoryRepository).delete(existingWorkHistory);

        // when
        calendarService.deleteWorkHistory(workHistoryId, member);

        // then
        // workHistoryRepository.delete 메서드가 인자값(existingWorkHistory)으로 1번 호출되었는지 검증
        verify(workHistoryRepository, times(1)).delete(existingWorkHistory);
    }

    @Test
    @DisplayName("개인 WorkSummary 조회 성공")
    void findWorkSummaryPersonalSuccess() {
        // given
        int year = 2025;
        int month = 8;
        Member member = Member.builder().sabun("123456").build();
        WorkSummary summary = WorkSummary.builder().id(1L).year(year).month(month).member(member).build();

        when(workSummaryRepository.findByYearAndMonthAndMember_Sabun(year, month, member.getSabun()))
                .thenReturn(List.of(summary));

        // when
        List<WorkSummaryDto> result = calendarService.findWorkSummaryPersonal(year, month, member);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("팀 WorkSummary 조회 성공")
    void findTeamWorkSummarySuccess() {
        // given
        int year = 2025;
        int month = 8;
        Member member = Member.builder().sabun("123456").build();
        WorkSummary summary = WorkSummary.builder().id(1L).year(year).month(month).member(member).build();

        when(workSummaryRepository.findTeamWorkSummaryByYearAndMonth(year, month, member.getSabun()))
                .thenReturn(List.of(summary));

        // when
        List<WorkSummaryDto> result = calendarService.findTeamWorkSummary(year, month, member);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }


    @Test
    @DisplayName("WorkSummary 저장 성공")
    void saveWorkSummarySuccess() {
        // given
        Member member = Member.builder().sabun("123456").build();
        WorkSummaryDto dto = WorkSummaryDto.builder()
                .year(2025).month(8).summary("테스트 요약").isShare(true).build();

        WorkSummary summaryToSave = WorkSummary.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .summary(dto.getSummary())
                .isShare(dto.getIsShare())
                .member(member)
                .build();

        // save 메서드가 호출되면 저장된 객체(ID가 부여된)를 반환하도록 설정
        when(workSummaryRepository.save(any(WorkSummary.class))).thenReturn(summaryToSave);

        // when
        WorkSummary savedSummary = calendarService.saveWorkSummary(dto, member);

        // then
        assertThat(savedSummary.getSummary()).isEqualTo("테스트 요약");
        assertThat(savedSummary.getMember().getSabun()).isEqualTo("123456");
    }

    @Test
    @DisplayName("WorkSummary 삭제 성공")
    void deleteWorkSummarySuccess() {
        // given
        Long summaryId = 1L;
        Member member = Member.builder().id("tester").build();
        WorkSummary existingSummary = WorkSummary.builder().id(summaryId).member(member).build();

        when(workSummaryRepository.findById(summaryId)).thenReturn(Optional.of(existingSummary));
        doNothing().when(workSummaryRepository).deleteById(summaryId);

        // when
        calendarService.deleteWorkSummary(summaryId, member);

        // then
        verify(workSummaryRepository, times(1)).deleteById(summaryId);
    }

    @Test
    @DisplayName("WorkSummary 공유 여부 수정 성공")
    void updateWorkSummarySuccess() {
        // given
        Long summaryId = 1L;
        Member member = Member.builder().id("tester").build();
        WorkSummaryDto dto = WorkSummaryDto.builder().id(summaryId).isShare(false).build();
        WorkSummary existingSummary = WorkSummary.builder().id(summaryId).member(member).isShare(true).build();

        when(workSummaryRepository.findById(summaryId)).thenReturn(Optional.of(existingSummary));

        // when
        WorkSummary updatedSummary = calendarService.updateWorkSummary(dto, member);

        // then
        assertThat(updatedSummary.getIsShare()).isFalse();
    }
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
                .role(MemberRole.ROLE_LEADER)
                .build();

        //when
        WorkHistory workHistoryRegister = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(workHistoryDto.getStartDate())
                                .endDate(workHistoryDto.getEndDate())
                                .build()
                )
                .title(workHistoryDto.getTitle())
                .content(workHistoryDto.getContent())
                .member(member)
                .team(Team.builder().id(1L).teamName("TEAM1").build())
                .build();

        when(workHistoryRepository.save(Mockito.any(WorkHistory.class))).thenReturn(workHistoryRegister);
        WorkHistory savedWorkHistory = calendarService.saveWorkHistory(workHistoryDto, member);


        // then
        assertThat(savedWorkHistory.getWorkRecordDate().getStartDate()).isEqualTo(workHistoryDto.getStartDate());
        assertThat(savedWorkHistory.getWorkRecordDate().getEndDate()).isEqualTo(workHistoryDto.getEndDate());
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
                .role(MemberRole.ROLE_LEADER)
                .build();

        //when
        WorkHistory workHistoryRegister = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(workHistoryDto.getStartDate())
                                .endDate(workHistoryDto.getEndDate())
                                .build()
                )
                .title(workHistoryDto.getTitle())
                .content(workHistoryDto.getContent())
                .member(member)
                .build();

        when(workHistoryRepository.save(Mockito.any(WorkHistory.class))).thenReturn(workHistoryRegister);
        WorkHistory savedWorkHistory = calendarService.saveWorkHistory(workHistoryDto, member);


        // then
        assertThat(savedWorkHistory.getWorkRecordDate().getStartDate()).isEqualTo(workHistoryDto.getStartDate());
        assertThat(savedWorkHistory.getWorkRecordDate().getEndDate()).isEqualTo(workHistoryDto.getEndDate());
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
                .role(MemberRole.ROLE_LEADER)
                .build();

        Team team1 = Team.builder()
                .teamName("TEAM1")
                .build();

        List<WorkHistory> workHistory = new ArrayList<>();
        List<WorkHistoryDto> workHistoryDto = new ArrayList<>();

        WorkHistory test1 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 3, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 8, 0, 0, 0))
                                .build()
                )
                .title("test1")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test2 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 10, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 19, 0, 0, 0))
                                .build()
                )
                .title("test2")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test3 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 20, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.MAY, 2, 0, 0, 0))
                                .build()
                )
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

        when(workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay(), member.getSabun())).thenReturn(workHistory);


        // when
        List<WorkHistoryDto> findWorkHistories = calendarService.findWorkHistoryByDate(startDate, endDate, member);

        assertThat(findWorkHistories.get(0).getTitle()).isEqualTo(workHistoryDto.get(0).getTitle());
        assertThat(findWorkHistories.get(1).getTitle()).isEqualTo(workHistoryDto.get(1).getTitle());
        assertThat(findWorkHistories.get(2).getTitle()).isEqualTo(workHistoryDto.get(2).getTitle());
    }
}