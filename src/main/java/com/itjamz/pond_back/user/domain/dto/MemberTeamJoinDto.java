package com.itjamz.pond_back.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class MemberTeamJoinDto {

    private List<String> memberSabun = new ArrayList<>();
    private Long teamId;
}
