package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.MemberTeamJoinDto;
import com.itjamz.pond_back.user.domain.dto.TeamDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import com.itjamz.pond_back.user.domain.entity.MemberTeamId;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.MemberRepository;
import com.itjamz.pond_back.user.repository.MemberTeamRepository;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;

    @Transactional
    public Team teamRegister(Team team, Member member){
        if (teamRepository.findTeamByTeamName(team.getTeamName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[팀 등록 실패] 이미 존재하는 팀");
        }
        Team savedTeam = teamRepository.save(team);

        // 본인 사번
        List<String> sabun = new ArrayList<>();
        sabun.add(member.getSabun());

        MemberTeamJoinDto memberTeamJoinDto = new MemberTeamJoinDto(sabun, savedTeam.getId());
        this.joinTeam(memberTeamJoinDto);

        return savedTeam;
    }

    @Transactional
    public List<MemberTeam> joinTeam(MemberTeamJoinDto memberTeamJoinDto) {
        Team findTeam = teamRepository.findById(memberTeamJoinDto.getTeamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "[팀 조인 실패] 존재하지 않는 팀 번호"));

        List<String> memberSabuns = memberTeamJoinDto.getMemberSabun();
        List<Member> members = memberRepository.findBySabunIn(memberSabuns);

        // 조회된 멤버 수가 요청된 사번 수와 다르면, 존재하지 않는 사번이 있다는 의미
        if (members.size() != memberSabuns.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[팀 조인 실패] 존재하지 않는 사원이 포함되어 있습니다.");
        }

        List<MemberTeam> memberTeams = members.stream()
                .map(member -> {
                    MemberTeamId memberTeamId = new MemberTeamId(member.getSabun(), findTeam.getId());
                    return MemberTeam.builder()
                            .id(memberTeamId)
                            .member(member)
                            .team(findTeam)
                            .build();
                })
                .collect(Collectors.toList());
        
        return memberTeamRepository.saveAll(memberTeams); 
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getTeamMembersForUser(String sabun) {
        List<Member> members = memberRepository.findTeamMembersByMemberSabun(sabun);
        return members.stream()
                .map(MemberDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Team> getTeams(Member member) {

        return teamRepository.findTeamsByMemberSabun(member.getSabun());
    }
}
