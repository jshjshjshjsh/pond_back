package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
import com.itjamz.pond_back.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 생성 성공")
    void memberRegisterSuccess() {
        // given (테스트 준비)
        // 3. 서비스에 전달할 때는 '암호화되지 않은' 원본 비밀번호를 가진 객체를 준비합니다.
        Member memberToRegister = Member.builder()
                .sabun("123456")
                .id("tester")
                .pw("pwtest") // 원본 비밀번호
                .name("테스터")
                .role(Member_Role.ROLE_NORMAL)
                .build();

        // 4. Mock 객체들의 행동을 미리 정의해줍니다.
        // - "pwtest"가 암호화되면 "encoded_password"가 될 것이라고 가정
        Mockito.when(passwordEncoder.encode("pwtest")).thenReturn("encoded_password");
        // - memberRepository.save가 호출되면, 인자로 받은 객체를 그대로 반환할 것이라고 가정
        Mockito.when(memberRepository.save(any(Member.class))).thenReturn(memberToRegister);
        // - 중복된 사번이 없다고 가정
        Mockito.when(memberRepository.findMemberByIdOrSabun(any(String.class), any(String.class))).thenReturn(Optional.empty());


        // when (실제 테스트 실행)
        Member registeredMember = memberService.memberRegister(memberToRegister);


        // then (결과 검증)
        // 5. 서비스 로직을 거친 후, 비밀번호가 우리가 예상한 "encoded_password"로 변경되었는지 확인합니다.
        assertThat(registeredMember.getPw()).isEqualTo("encoded_password");
        // 6. 역할(Role)이 정상적으로 부여되었는지 확인합니다.
        assertThat(registeredMember.getRole()).isEqualTo(Member_Role.ROLE_NORMAL);

        // 7. 의존 객체들의 메서드가 정확히 호출되었는지 추가로 검증합니다.
        verify(passwordEncoder).encode("pwtest"); // pwtest로 encode가 호출되었는가?
        verify(memberRepository).save(any(Member.class)); // save가 호출되었는가?
    }
}