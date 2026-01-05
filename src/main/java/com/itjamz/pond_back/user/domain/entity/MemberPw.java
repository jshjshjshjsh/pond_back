package com.itjamz.pond_back.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MemberPw {

    @NotNull
    @Column(name = "pw")
    private String pw;

    public MemberPw(String pw) {
        if (pw.length() < 6)
            throw new IllegalArgumentException("패스워드 규칙에 맞지 않습니다.");
        this.pw = pw;
    }

    public void encodingPw(PasswordEncoder encoder){
        this.pw = encoder.encode(this.pw);
    }

    public boolean match(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.pw);
    }
}
