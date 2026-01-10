package com.itjamz.pond_back.user.infra.repository;


import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import com.itjamz.pond_back.user.repository.MemberTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberTeamRepositoryImpl implements MemberTeamRepository {

    private final MemberTeamJpaRepository memberTeamRepository;

    @Override
    public MemberTeam save(MemberTeam memberTeam) {
        return memberTeamRepository.save(memberTeam);
    }

    @Override
    public List<MemberTeam> saveAll(List<MemberTeam> memberTeams) {
        return memberTeamRepository.saveAll(memberTeams);
    }

    @Override
    public Optional<MemberTeam> findMemberTeamByIdMember(String sabun) {
        return memberTeamRepository.findMemberTeamByIdMember(sabun);
    }
}
