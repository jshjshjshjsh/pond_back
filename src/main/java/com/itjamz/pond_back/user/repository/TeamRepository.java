package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findTeamByTeamName(String teamName);

    @Query("SELECT mt.team FROM MemberTeam mt WHERE mt.member.sabun = :sabun")
    List<Team> findTeamsByMemberSabun(@Param("sabun") String sabun);
}
