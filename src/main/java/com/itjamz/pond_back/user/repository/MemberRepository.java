package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Member;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findMemberBySabun(String sabun);
    Optional<Member> findMemberById(String id);
    Optional<Member> findMemberByIdOrSabun(String id, String sabun);
    List<Member> findTeamMembersByMemberSabun(@Param("sabun") String sabun);
    List<Member> findBySabunIn(List<String> sabuns);
}
