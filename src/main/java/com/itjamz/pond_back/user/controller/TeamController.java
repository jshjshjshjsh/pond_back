package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.MemberTeamJoinDto;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/leader/register")
    public ResponseEntity<Void> teamRegister(@RequestBody Team team) {
        teamService.teamRegister(team);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/leader/{teamId}/members")
    public ResponseEntity<Void> joinTeam(@PathVariable Long teamId, @RequestBody MemberTeamJoinDto memberTeamJoinDto) {
        memberTeamJoinDto.setTeamId(teamId);
        teamService.joinTeam(memberTeamJoinDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-teams/members")
    public ResponseEntity<List<MemberDto>> getMyTeamMembers(@AuthenticationPrincipal CustomUserDetails userDetails){

        return ResponseEntity.ok(teamService.getTeamMembersForUser(userDetails.getMember().getSabun()));
    }

}
