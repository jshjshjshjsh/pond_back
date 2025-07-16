package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
