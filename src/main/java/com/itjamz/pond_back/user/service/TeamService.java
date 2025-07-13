package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public Team teamRegister(Team team){
        if (teamRepository.findTeamByTeamName(team.getTeamName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[팀 등록 실패] 이미 존재하는 팀");
        }

        return teamRepository.save(team);
    }
}
