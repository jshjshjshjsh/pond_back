package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.dto.MemberTeamJoinDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.MemberRepository;
import com.itjamz.pond_back.user.repository.MemberTeamRepository;
import com.itjamz.pond_back.user.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks // 1. 테스트 대상 클래스. @Mock으로 선언된 객체들을 여기에 주입합니다.
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberTeamRepository memberTeamRepository;

    @Test
    @DisplayName("팀 생성 성공")
    void teamRegisterSuccess(){

        // given
        Team team = Team.builder()
                .teamName("testTeam1")
                .build();
        ReflectionTestUtils.setField(team, "id", 1L);

        Mockito.when(teamRepository.save(team)).thenReturn(team);

        // when
        Team teamRegister = teamService.teamRegister(team);

        // then
        assertThat(teamRegister.getId()).isEqualTo(1L);
        assertThat(teamRegister.getTeamName()).isEqualTo(team.getTeamName());
    }

    @Test
    @DisplayName("팀 소속 성공")
    void teamJoinSuccess() {
        // given (준비)
        // 1. 테스트에 사용할 DTO 생성
        MemberTeamJoinDto memberTeamJoinDto = MemberTeamJoinDto.builder()
                .teamId(1L)
                .memberSabun(List.of("123456", "123457"))
                .build();

        // 2. Mock 객체들의 행동을 미리 정의 (Stubbing)
        Team fakeTeam = Team.builder().id(1L).teamName("Test Team").build();
        Mockito.when(teamRepository.findById(1L)).thenReturn(Optional.of(fakeTeam));

        Member fakeMember1 = Member.builder().sabun("123456").build();
        Member fakeMember2 = Member.builder().sabun("123457").build();
        Mockito.when(memberRepository.findMemberBySabun("123456")).thenReturn(Optional.of(fakeMember1));
        Mockito.when(memberRepository.findMemberBySabun("123457")).thenReturn(Optional.of(fakeMember2));

        // saveAll이 호출되면, 인자로 받은 리스트를 그대로 반환하도록 설정
        // (실제로는 저장 로직 후의 객체를 반환하므로, 여기서는 간단하게 anyList()를 사용)
        Mockito.when(memberTeamRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));


        // when (실행)
        List<MemberTeam> registeredMemberTeams = teamService.joinTeam(memberTeamJoinDto);


        // then (검증)
        // 1. 예상한 수만큼의 MemberTeam 객체가 생성되었는지 확인
        assertThat(registeredMemberTeams).hasSize(2);

        // 2. 각 MemberTeam 객체의 정보가 올바르게 설정되었는지 확인
        assertThat(registeredMemberTeams.get(0).getTeam().getId()).isEqualTo(1L);
        assertThat(registeredMemberTeams.get(0).getMember().getSabun()).isEqualTo("123456");

        assertThat(registeredMemberTeams.get(1).getTeam().getId()).isEqualTo(1L);
        assertThat(registeredMemberTeams.get(1).getMember().getSabun()).isEqualTo("123457");
    }

    @Test
    @DisplayName("팀 소속 실패 - 존재하지 않는 팀")
    void teamJoinNotExistsTeam(){

        // given
        MemberTeamJoinDto memberTeamJoinDto = MemberTeamJoinDto.builder()
                .teamId(-1L)
                .memberSabun(List.of("123456", "123457"))
                .build();

        Mockito.when(teamRepository.findById(-1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.joinTeam(memberTeamJoinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[팀 조인 실패] 존재하지 않는 팀 번호");
    }

    @Test
    @DisplayName("팀 소속 실패 - 존재하지 않는 사번")
    void teamJoinNotExistsSabun(){

        // given
        MemberTeamJoinDto memberTeamJoinDto = MemberTeamJoinDto.builder()
                .teamId(1L)
                .memberSabun(List.of("123456", "123457"))
                .build();

        Team fakeTeam = Team.builder().id(1L).teamName("Test Team").build();

        Mockito.when(memberRepository.findMemberBySabun("123456")).thenReturn(Optional.empty());
        Mockito.when(teamRepository.findById(1L)).thenReturn(Optional.of(fakeTeam));

        // when & then
        assertThatThrownBy(() -> teamService.joinTeam(memberTeamJoinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[팀 조인 실패] 존재하지 않는 사원 번호");
    }
}