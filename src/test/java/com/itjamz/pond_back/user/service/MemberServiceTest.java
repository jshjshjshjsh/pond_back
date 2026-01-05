package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.k6.repository.MileageRepository;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberPw;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import com.itjamz.pond_back.user.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MileageRepository mileageRepository;

    private Member generateMember(){
        return Member.builder()
                .sabun("123456")
                .id("tester")
                .pw(new MemberPw("pwtest")) // 원본 비밀번호
                .name("테스터")
                .role(MemberRole.ROLE_NORMAL)
                .build();
    }

    @Test
    @DisplayName("멤버 비밀번호 변경 실패 - 존재하지 않는 사번")
    void memberChangeInfoFail(){
        // given
        Member member = this.generateMember();
        MemberDto memberDto = MemberDto.builder()
                .pw("changePw")
                .role(MemberRole.ROLE_LEADER)
                .build();

        // when
        when(memberRepository.findMemberBySabun(member.getSabun())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> memberService.memberChangeInfo(member, memberDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[계정 조회 실패] 존재하지 않는 ID 또는 사번");

    }

    @Test
    @DisplayName("멤버 신분, 비밀번호 변경")
    void memberChangeInfo() {
        // given
        Member member = this.generateMember();
        MemberDto memberDto = MemberDto.builder()
                .pw("changePw")
                .role(MemberRole.ROLE_LEADER)
                .build();
        when(memberRepository.findMemberBySabun(member.getSabun())).thenReturn(Optional.of(member));
        when(passwordEncoder.encode("changePw")).thenReturn("encoded_changePw");

        // when
        Member changedMember = memberService.memberChangeInfo(member, memberDto);

        // then
        assertThat(changedMember.getSabun()).isEqualTo(member.getSabun());
        assertThat(changedMember.getPw().getPw()).isEqualTo("encoded_changePw");
        assertThat(changedMember.getRole()).isEqualTo(MemberRole.ROLE_LEADER);
    }

    @Test
    @DisplayName("멤버 조회")
    void findMemberById(){
        // given
        Member member = this.generateMember();
        ReflectionTestUtils.setField(member.getPw(),"pw","encoded_pwtest");

        // when
        when(memberRepository.findMemberById("tester")).thenReturn(Optional.of(member));
        Optional<Member> findMember = memberService.findMemberById("tester");

        // then
        assertThat(findMember.get().getId()).isEqualTo(member.getId());
        assertThat(findMember.get().getSabun()).isEqualTo(member.getSabun());
        assertThat(findMember.get().getPw().getPw()).isEqualTo("encoded_pwtest");

    }

    @Test
    @DisplayName("멤버 생성 실패 - 이미 존재하는 사번")
    void memberRegisterFailExists(){
        // given
        Member member = this.generateMember();


        // when
        when(memberRepository.findMemberByIdOrSabun(any(String.class), any(String.class))).thenReturn(Optional.ofNullable(member));
        assertThatThrownBy(() -> memberService.memberRegister(member))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[회원가입 실패] 이미 존재하는 ID 또는 사번");
    }

    @Test
    @DisplayName("멤버 생성 성공")
    void memberRegisterSuccess() {
        // given (테스트 준비)
        Member member = this.generateMember();

        // 4. Mock 객체들의 행동을 미리 정의해줍니다.
        // - "pwtest"가 암호화되면 "encoded_password"가 될 것이라고 가정
        when(passwordEncoder.encode("pwtest")).thenReturn("encoded_password");
        // - memberRepository.save가 호출되면, 인자로 받은 객체를 그대로 반환할 것이라고 가정
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        // - 중복된 사번이 없다고 가정
        when(memberRepository.findMemberByIdOrSabun(any(String.class), any(String.class))).thenReturn(Optional.empty());


        // when (실제 테스트 실행)
        Member registeredMember = memberService.memberRegister(member);


        // then (결과 검증)
        // 5. 서비스 로직을 거친 후, 비밀번호가 우리가 예상한 "encoded_password"로 변경되었는지 확인합니다.
        assertThat(registeredMember.getPw().getPw()).isEqualTo("encoded_password");
        // 6. 역할(Role)이 정상적으로 부여되었는지 확인합니다.
        assertThat(registeredMember.getRole()).isEqualTo(MemberRole.ROLE_NORMAL);

        // 7. 의존 객체들의 메서드가 정확히 호출되었는지 추가로 검증합니다.
        verify(passwordEncoder).encode("pwtest"); // pwtest로 encode가 호출되었는가?
        verify(memberRepository).save(any(Member.class)); // save가 호출되었는가?
    }
}