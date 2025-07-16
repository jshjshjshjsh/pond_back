package com.itjamz.pond_back.user.domain.dto;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {

    private String sabun;
    private String id;
    private String pw;
    private String name;
    private Member_Role role;

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .sabun(member.getSabun())
                .id(member.getId())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }
}
