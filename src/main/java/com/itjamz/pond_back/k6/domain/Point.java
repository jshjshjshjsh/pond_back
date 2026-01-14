package com.itjamz.pond_back.k6.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/**
 * "도메인 주도 개발 시작하기" 책을 읽고 테스트 용도로 만들어진 엔티티입니다.
 * Chapter 8. 애그리거트 트랜잭션 관리 실습을 위해,
 * 기존 Mileage 도메인과 별도로 낙관적 락(Optimistic Lock)과 비관적 락(Pessimistic Lock) 성능 비교를 수행합니다.
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long amount;

    @Column(nullable = false)
    private String memberId;

    @Version
    private Long version;

    // 입금
    public Long deposit(Long amount){
        this.amount += amount;
        return this.amount;
    }

    // 출금
    public Long withdraw(Long amount){
        if (this.amount < amount){
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        this.amount -= amount;
        return this.amount;
    }
}