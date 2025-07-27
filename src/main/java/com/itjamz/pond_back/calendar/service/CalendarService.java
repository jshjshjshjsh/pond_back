package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.calendar.repository.WorkSummaryRepository;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final TeamRepository teamRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final WorkSummaryRepository workSummaryRepository;

    @Transactional
    public void deleteWorkHistory(Long id, Member member) {
        Optional<WorkHistory> findWorkHistory = workHistoryRepository.findById(id);
        if (findWorkHistory.isEmpty() || !findWorkHistory.get().getMember().getId().equals(member.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[workhistory 조회 실패] 조회 실패");

        workHistoryRepository.delete(findWorkHistory.get());
    }

    @Transactional
    public void updateWorkHistory(Long id, WorkHistoryDto workHistoryDto, Member member) {
        // 먼저 본인의 정상적인 workhistory인지 확인
        Optional<WorkHistory> findWorkHistory = workHistoryRepository.findById(id);
        if (findWorkHistory.isEmpty() || !findWorkHistory.get().getMember().getId().equals(member.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[workhistory 조회 실패] 조회 실패");

        // 다음 if else 문으로 update
        // team은 find를 해주고 넣어줘야 함
        Team team = null;
        if (workHistoryDto.getTeam() != null)
            team = teamRepository.findById(workHistoryDto.getTeam().getId()).orElse(null);
        findWorkHistory.get().patchWorkHistory(workHistoryDto, team);
    }

    public WorkHistory saveWorkHistory(WorkHistoryDto workHistoryDto, Member member) {
        Team team = null;
        if (workHistoryDto.getTeam() != null && workHistoryDto.getTeam().getId() != null) {
            team = teamRepository.findById(workHistoryDto.getTeam().getId()).orElse(null);
        }

        WorkHistory workHistory = WorkHistory.builder()
                .title(workHistoryDto.getTitle())
                .content(workHistoryDto.getContent())
                .startDate(workHistoryDto.getStartDate())
                .endDate(workHistoryDto.getEndDate())
                .isShare(workHistoryDto.getIsShare())
                .member(member)
                .team(team)
                .build();


        return workHistoryRepository.save(workHistory);
    }

    public List<WorkHistoryDto> findWorkHistoryByDate(LocalDate startDate, LocalDate endDate, Member member) {
        List<WorkHistory> workHistories = workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay(), member.getSabun());
        return workHistories.stream().map(WorkHistoryDto::from).collect(Collectors.toList());
    }
}
