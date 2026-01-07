package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberTeam {

    @EmbeddedId
    private MemberTeamId id;
}
