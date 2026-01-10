package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberPw;
import com.itjamz.pond_back.user.infra.repository.MemberJpaRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    // 테스트를 위한 별도의 설정 클래스
    @TestConfiguration
    static class TestConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(); // 실제 사용하는 PasswordEncoder 구현체
        }
    }

    @Test
    @DisplayName("멤버 저장 성공")
    void memberRegisterSuccess(){
        // given
        Member member = Member
                .builder()
                .sabun("123456")
                .id("tester")
                .pw(new MemberPw(passwordEncoder.encode("pwtest")))
                .name("테스터").build();

        // when
        Member memberRegister = memberRepository.save(member);

        // then
        assertThat(memberRegister.getSabun()).isEqualTo(member.getSabun());
        assertThat(memberRegister.getRole()).isEqualTo(member.getRole());
        assertThat(memberRegister.getPw()).isEqualTo(member.getPw());

    }

    @Test
    @DisplayName("맴버 필드 오류 발생")
    void memberRegisterFail(){
        // given
        Member member = Member
                .builder()
                .sabun("123456")
                .id("tester")
                .pw(new MemberPw(passwordEncoder.encode("pwtest")))
                .build();

        // when & then
        assertThatThrownBy(() -> {
            memberRepository.save(member);
            memberRepository.flush();
        })
        .isInstanceOf(ConstraintViolationException.class); // 예외 타입 검증

    }
}