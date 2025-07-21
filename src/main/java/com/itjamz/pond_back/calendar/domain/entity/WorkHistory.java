package com.itjamz.pond_back.calendar.domain.entity;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Team;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkHistory {


    @Id @GeneratedValue
    private Long id;

    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    private String title;
    private String content;
    private Boolean isShare;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sabun")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_teamCode")
    private Team team;

    public WorkHistory patchWorkHistory(WorkHistoryDto workHistoryDto, Team team){

        if (workHistoryDto.getStartDate() != null)
            this.startDate = workHistoryDto.getStartDate();
        if (workHistoryDto.getEndDate() != null)
            this.endDate = workHistoryDto.getEndDate();
        if (workHistoryDto.getTitle() != null)
            this.title = workHistoryDto.getTitle();
        if (workHistoryDto.getContent() != null)
            this.content = workHistoryDto.getContent();
        if (workHistoryDto.getIsShare() != null)
            this.isShare = workHistoryDto.getIsShare();
        this.team = team;

        return this;
    }
}
