package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@Getter
@EqualsAndHashCode
@ToString
public class MemberPw {

    @NotNull
    @Column(name = "pw")
    private String pw;

    protected MemberPw() {}

    private MemberPw(String encodedPw) {
        this.pw = encodedPw;
    }

    public static MemberPw create(String rawPw, PasswordEncoder encoder) {
        if (rawPw == null || rawPw.length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자리 이상이어야 합니다.");
        }

        return new MemberPw(encoder.encode(rawPw));
    }

    public boolean match(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.pw);
    }
}
