package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberTeam {

    @EmbeddedId
    private MemberTeamId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("member") // Maps the 'member' field of MemberTeamId
    @JoinColumn(name = "member_sabun")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("team") // Maps the 'team' field of MemberTeamId
    @JoinColumn(name = "team_id")
    private Team team;
}
