package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findMemberBySabun(String sabun);
    Optional<Member> findMemberById(String id);
    Optional<Member> findMemberByIdOrSabun(String id, String sabun);

    @EntityGraph(attributePaths = {"memberTeams.team"})
    @Query("SELECT DISTINCT m FROM Member m " +
            "JOIN FETCH m.memberTeams mt " +
            "JOIN FETCH mt.team t " +
            "WHERE t.id IN (SELECT mt2.team.id FROM MemberTeam mt2 WHERE mt2.member.sabun = :sabun)")
    List<Member> findTeamMembersByMemberSabun(@Param("sabun") String sabun);
}
