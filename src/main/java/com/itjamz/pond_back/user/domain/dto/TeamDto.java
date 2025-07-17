package com.itjamz.pond_back.user.domain.dto;

import com.itjamz.pond_back.user.domain.entity.Team;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamDto {

    private Long id;
    private String teamName;
    private LocalDateTime createTime;


    public static TeamDto from(Team team) {
        return TeamDto.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .createTime(team.getCreateTime())
                .build();
    }
}
