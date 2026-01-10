package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository{

    private final TeamJpaRepository teamRepository;

    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    @Override
    public Optional<Team> findTeamByTeamName(String teamName){
        return teamRepository.findTeamByTeamName(teamName);
    };

    @Override
    public List<Team> findTeamsByMemberSabun(@Param("sabun") String sabun){
        return teamRepository.findTeamsByMemberSabun(sabun);
    };
}
