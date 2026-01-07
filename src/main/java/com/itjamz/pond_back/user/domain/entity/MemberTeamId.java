package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberTeamId implements Serializable {
    private String member; // Member's PK (sabun)
    private Long team;     // Team's PK (id)
}
