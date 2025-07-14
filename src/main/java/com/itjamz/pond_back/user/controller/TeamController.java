package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.user.domain.dto.MemberTeamJoinDto;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/team/leader/register")
    public ResponseEntity<?> teamRegister(@RequestBody Team team) {
        teamService.teamRegister(team);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/team/leader/{teamId}/members")
    public ResponseEntity<?> joinTeam(@PathVariable Long teamId, @RequestBody MemberTeamJoinDto memberTeamJoinDto) {
        memberTeamJoinDto.setTeamId(teamId);
        teamService.joinTeam(memberTeamJoinDto);

        return ResponseEntity.ok().build();
    }
}
