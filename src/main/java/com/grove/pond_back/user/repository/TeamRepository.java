package com.grove.pond_back.user.repository;

import com.grove.pond_back.user.domain.entity.Team;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {

    Team save(Team team);
    Optional<Team> findById(Long id);
    Optional<Team> findTeamByTeamName(String teamName);
    List<Team> findTeamsByMemberSabun(@Param("sabun") String sabun);
}
