package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String sabun;
    @NotNull
    @Column(unique = true)
    private String id;
    @NotNull
    private String pw;
    @NotNull
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @NotNull
    private Member_Role role;

    public void encodedPw(String pw){
        this.pw = pw;
    }
}
