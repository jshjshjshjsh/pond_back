package com.grove.pond_back.user.domain.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class MemberTeamId implements Serializable {
    private String member;
    private Long team;

    protected MemberTeamId() {}
}
