package com.itjamz.pond_back.user.domain.dto;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.LazyInitializationException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemberDto {

    private String sabun;
    private String id;
    private String pw;
    private String name;
    private Member_Role role;
    private List<TeamDto> teams;

    public static MemberDto from(Member member) {
        MemberDtoBuilder builder = MemberDto.builder()
                .sabun(member.getSabun())
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole());

        List<TeamDto> teamDtos = member.getMemberTeams().stream()
                .map(memberTeam -> TeamDto.from(memberTeam.getTeam()))
                .collect(Collectors.toList());
        builder.teams(teamDtos);

        return builder.build();
    }
}
