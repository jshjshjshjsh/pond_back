package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/team/register")
    public ResponseEntity<?> teamRegister(@RequestBody Team team) {
        teamService.teamRegister(team);

        return ResponseEntity.ok().build();
    }
}
