package com.itjamz.pond_back.user.infra.repository;

import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import com.itjamz.pond_back.user.domain.entity.MemberTeamId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTeamJpaRepository extends JpaRepository<MemberTeam, MemberTeamId> {
    Optional<MemberTeam> findMemberTeamByIdMember(String sabun);
}
