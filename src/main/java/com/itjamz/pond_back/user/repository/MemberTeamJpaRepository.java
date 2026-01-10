package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface MemberTeamJpaRepository extends JpaRepository<MemberTeam, Long> {
    Optional<MemberTeam> findMemberTeamByIdMember(String sabun);
}
