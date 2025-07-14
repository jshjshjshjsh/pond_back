package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id @GeneratedValue
    private Long teamCode;
    @Column(unique = true)
    private String teamName;
    @CreatedDate
    private LocalDateTime createTime;
}
