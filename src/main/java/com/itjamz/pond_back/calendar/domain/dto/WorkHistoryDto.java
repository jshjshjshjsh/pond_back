package com.itjamz.pond_back.calendar.domain.dto;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.Month;

@Getter
@Builder
public class WorkHistoryDto {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String title;
    private String content;

    private MemberDto member;
    private TeamDto team;

    public static WorkHistoryDto from(WorkHistory workHistory) {
        return WorkHistoryDto.builder()
                .startDate(workHistory.getStartDate())
                .endDate(workHistory.getStartDate())
                .title(workHistory.getTitle())
                .content(workHistory.getContent())
                .member(MemberDto.from(workHistory.getMember()))
                .team(TeamDto.from(workHistory.getTeam()))
                .build();
    }
}
