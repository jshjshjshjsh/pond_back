package com.itjamz.pond_back.user.domain.entity;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private String sabun;
    @NotNull
    @Column(unique = true)
    private String id;
    @Embedded
    private MemberPw pw;
    @NotNull
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @NotNull
    private MemberRole role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberTeam> memberTeams = new ArrayList<>();

    public void encodedPw(String pw, PasswordEncoder encoder){
        this.pw = new MemberPw(pw);
        this.pw.encodingPw(encoder);
    }

    public void changeInfo(MemberDto memberDto) {
        this.role = memberDto.getRole();
    }
}
