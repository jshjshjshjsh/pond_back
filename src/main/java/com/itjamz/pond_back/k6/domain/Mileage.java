package com.itjamz.pond_back.k6.domain;

import com.itjamz.pond_back.user.domain.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mileage {

    @Id @GeneratedValue
    private Long id;

    @ColumnDefault("0")
    private Long amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Long deposit(Long amount) {
        this.amount += amount;
        return this.amount;
    }

    public Long withdraw(Long amount) {
        this.amount -= amount;
        return this.amount;
    }
}
