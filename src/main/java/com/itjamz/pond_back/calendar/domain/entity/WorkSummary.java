package com.itjamz.pond_back.calendar.domain.entity;

import com.itjamz.pond_back.user.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSummary {

    @Id @GeneratedValue
    private Long id;
    private int year;
    private int month;
    @Lob
    private String summary;
    private Boolean isShare;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_sabun")
    private Member member;

    public void changeIsShare(Boolean isShare) {
        this.isShare = isShare;
    }
}
