package com.itjamz.pond_back.user.infra.repository;

import com.itjamz.pond_back.user.domain.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, String> {

    Optional<Member> findMemberBySabun(String sabun);
    Optional<Member> findMemberById(String id);
    Optional<Member> findMemberByIdOrSabun(String id, String sabun);

    @EntityGraph(attributePaths = {"memberTeams.team"})
    @Query("SELECT m FROM Member m " +
            "WHERE m.sabun IN (" +
            "    SELECT mt_other.id.member FROM MemberTeam mt_other " +
            "    WHERE mt_other.id.team IN (" +
            "        SELECT mt_me.id.team FROM MemberTeam mt_me " +
            "        WHERE mt_me.id.member = :sabun" +
            "    )" +
            ")")
    List<Member> findTeamMembersByMemberSabun(@Param("sabun") String sabun);
    List<Member> findBySabunIn(List<String> sabuns);
}
