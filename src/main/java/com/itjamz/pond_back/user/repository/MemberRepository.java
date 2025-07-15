package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findMemberBySabun(String sabun);
    Optional<Member> findMemberById(String id);
    List<Member> findAllBySabunIn(List<String> sabun);
}
