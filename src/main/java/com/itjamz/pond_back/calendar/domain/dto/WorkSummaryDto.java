package com.itjamz.pond_back.calendar.domain.dto;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
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

}
