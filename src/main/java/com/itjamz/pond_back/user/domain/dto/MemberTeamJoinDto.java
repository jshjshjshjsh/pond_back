package com.itjamz.pond_back.user.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MemberTeamJoinDto {

    private List<String> memberSabun = new ArrayList<>();
    private Long teamId;
}
