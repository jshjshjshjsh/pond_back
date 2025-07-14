package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TeamServiceTest {

    TeamService teamService;

    @MockitoBean
    TeamRepository teamRepository;

    @BeforeEach
    void setUp(){
        teamService = new TeamService(teamRepository);
    }


    @Test
    @DisplayName("팀 생성 성공")
    void teamRegisterSuccess(){

        // given
        Team team = Team.builder().teamName("testTeam1").build();
        ReflectionTestUtils.setField(team, "teamCode", 1L);

        // when
        Mockito.when(teamRepository.save(team)).thenReturn(team);

        // then
        Team teamRegister = teamService.teamRegister(team);

        assertThat(teamRegister.getTeamCode()).isEqualTo(1L);
        assertThat(teamRegister.getTeamName()).isEqualTo(team.getTeamName());
    }
}