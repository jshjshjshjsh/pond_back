package com.itjamz.pond_back.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Team {

    @Id @GeneratedValue
    private Long teamCode;
    private String teamName;
    @CreatedDate
    private LocalDateTime createTime;
}
