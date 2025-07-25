package com.itjamz.pond_back.calendar.domain.dto;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Month;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkHistoryDto {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String title;
    private String content;
    private Boolean isShare;

    private MemberDto member;
    private TeamDto team;

    public static WorkHistoryDto from(WorkHistory workHistory) {
        return WorkHistoryDto.builder()
                .id(workHistory.getId())
                .startDate(workHistory.getStartDate())
                .endDate(workHistory.getEndDate())
                .title(workHistory.getTitle())
                .content(workHistory.getContent())
                .isShare(workHistory.getIsShare())
                .member(MemberDto.from(workHistory.getMember()))
                .team(workHistory.getTeam() != null ? TeamDto.from(workHistory.getTeam()) : null)
                .build();
    }
}
