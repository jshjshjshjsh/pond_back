package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findTeamByTeamName(String teamName);
}
