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

    public void encodedPw(String rawPw, PasswordEncoder encoder){
        MemberPw rawMemberPw = new MemberPw(rawPw);
        this.pw = rawMemberPw.encodingPw(encoder);
    }

    public void changeInfo(MemberDto memberDto) {
        this.role = memberDto.getRole();
    }
}
