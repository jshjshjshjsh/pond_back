package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    MemberService memberService;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @MockitoBean
    MemberRepository memberRepository;


    @BeforeEach
    void setUp(){
        memberService = new MemberService(passwordEncoder, memberRepository);
    }

    @Test
    @DisplayName("멤버 생성 성공")
    void memberRegisterSuccess() {

        // given
        Member member = Member.builder().sabun("123456").id("tester").pw("pwtest").name("테스터").build();
        ReflectionTestUtils.setField(member, "pw", passwordEncoder.encode("pwtest"));

        // when
        Mockito.when(memberRepository.save(member)).thenReturn(member);

        // then
        Member memberRegister = memberService.memberRegister(member);

        assertThat(memberRegister.getSabun()).isEqualTo(member.getSabun());
        assertThat(memberRegister.getRole()).isEqualTo(member.getRole());
        assertThat(memberRegister.getPw()).isEqualTo(member.getPw());

    }
}