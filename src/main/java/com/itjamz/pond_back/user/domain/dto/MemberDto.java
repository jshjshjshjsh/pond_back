package com.itjamz.pond_back.user.domain.dto;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberPw;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private String sabun;
    private String id;
    private String pw;
    private String name;
    private MemberRole role;
    private List<TeamDto> teams;

    public static MemberDto from(Member member) {
        MemberDtoBuilder builder = MemberDto.builder()
                .sabun(member.getSabun())
                .id(member.getId())
                .pw(member.getPw().getPw())
                .name(member.getName())
                .role(member.getRole());

        return builder.build();
    }
}
