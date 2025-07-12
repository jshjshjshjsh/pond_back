package com.itjamz.pond_back.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
//@Builder
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    private String sabun;
    @NotNull
    private String id;
    @NotNull
    private String pw;
    @NotNull
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @NotNull
    private Member_Role role;

    public void initRegister(String pw, Member_Role role){
        this.pw = pw;
        this.role = role;
    }
}
