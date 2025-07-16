package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final TeamRepository teamRepository;
    private final WorkHistoryRepository workHistoryRepository;

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
                .member(member)
                .team(team)
                .build();


        return workHistoryRepository.save(workHistory);
    }

    public List<WorkHistory> findWorkHistoryByDate(LocalDate startDate, LocalDate endDate) {
        return workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay());
        //List<WorkHistory> workHistories = workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay());
        //return workHistories.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private WorkHistoryDto convertToDto(WorkHistory workHistory) {
        return WorkHistoryDto.builder()
                .id(workHistory.getId())
                .title(workHistory.getTitle())
                .content(workHistory.getContent())
                .startDate(workHistory.getStartDate())
                .endDate(workHistory.getEndDate())
                .member(MemberDto.from(workHistory.getMember()))
                .team(workHistory.getTeam() != null ? TeamDto.from(workHistory.getTeam()) : null)
                .build();
    }
}
