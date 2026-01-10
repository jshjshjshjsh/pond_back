package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.MemberTeam;

import java.util.List;
import java.util.Optional;

public interface MemberTeamRepository {

    MemberTeam save(MemberTeam memberTeam);
    List<MemberTeam> saveAll(List<MemberTeam> memberTeams);
    Optional<MemberTeam> findMemberTeamByIdMember(String sabun);
}
