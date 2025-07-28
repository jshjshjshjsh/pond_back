package com.itjamz.pond_back.calendar.domain.dto;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkSummaryDto {

    private Long id;
    private int year;
    private int month;
    private String summary;
    private Boolean isShare;
    private MemberDto member;


    public static WorkSummaryDto from(WorkSummary workSummary) {
        return WorkSummaryDto.builder()
                .id(workSummary.getId())
                .year(workSummary.getYear())
                .month(workSummary.getMonth())
                .summary(workSummary.getSummary())
                .isShare(workSummary.getIsShare())
                .member(MemberDto.from(workSummary.getMember()))
                .build();
    }
}
