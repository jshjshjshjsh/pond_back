package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {
    Optional<MemberTeam> findMemberTeamByMember_Sabun(String sabun);
}
