package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.dto.MemberTeamJoinDto;
import com.itjamz.pond_back.user.domain.entity.*;
import com.itjamz.pond_back.user.repository.MemberJpaRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks // 1. 테스트 대상 클래스. @Mock으로 선언된 객체들을 여기에 주입합니다.
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MemberJpaRepository memberRepository;
    @Mock
    private MemberTeamRepository memberTeamRepository;

    private MemberTeamJoinDto generateMemberTeamJoinDto() {
        return MemberTeamJoinDto.builder()
                .teamId(1L)
                .memberSabun(List.of("123456", "123457"))
                .build();
    }

    private Team generateTeam(String teamName){
        return Team.builder()
                .id(1L)
                .teamName(teamName)
                .build();
    }

    private Member generateMember(String sabun, String id, String pw, String name){
        return Member.builder()
                .sabun(sabun)
                .id(id)
                .pw(new MemberPw(pw))
                .name(name)
                .role(MemberRole.ROLE_NORMAL)
                .build();
    }

    @Test
    @DisplayName("본인의 팀들 전부 가져오기")
    void getTeams(){
        // given
        Team team1 = this.generateTeam("testTeam1");
        Team team2 = this.generateTeam("testTeam2");
        Member member = this.generateMember("123456", "tester1", "pwtest", "테스터1");


        // when
        when(teamRepository.findTeamsByMemberSabun("123456")).thenReturn(List.of(team1, team2));
        List<Team> findTeams = teamService.getTeams(member);

        // then
        assertThat(findTeams).hasSize(2);
        assertThat(findTeams.get(0).getTeamName()).isEqualTo(team1.getTeamName());
        assertThat(findTeams.get(1).getTeamName()).isEqualTo(team2.getTeamName());
    }

    @Test
    @DisplayName("본인 소속된 팀의 유저정보 가져오기")
    void getTeamMembersForUser(){
        // given
        Member member1 = this.generateMember("123456", "tester1", "pwtest", "테스터1");
        Member member2 = this.generateMember("123457", "tester2", "pwtest", "테스터2");

        // when
        when(memberRepository.findTeamMembersByMemberSabun("123456")).thenReturn(List.of(member1, member2));
        List<MemberDto> members = teamService.getTeamMembersForUser("123456");

        // then
        assertThat(members).hasSize(2);
        assertThat(members.get(0).getSabun()).isEqualTo("123456");
        assertThat(members.get(1).getSabun()).isEqualTo("123457");

    }

    @Test
    @DisplayName("팀 생성 실패 - 이미 존재하는 팀명")
    void teamRegisterFail(){
        // given
        Team team = this.generateTeam("testTeam1");
        Member member = this.generateMember("123456", "tester", "pwtest", "테스터");

        // when
        when(teamRepository.findTeamByTeamName(team.getTeamName())).thenReturn(Optional.of(team));

        // then
        assertThatThrownBy(() -> teamService.teamRegister(team, member))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[팀 등록 실패] 이미 존재하는 팀");
    }

    @Test
    @DisplayName("팀 생성 성공")
    void teamRegisterSuccess(){

        // given
        Team team = this.generateTeam("testTeam1");
        Member member = this.generateMember("123456", "tester", "pwtest", "테스터");

        ReflectionTestUtils.setField(team, "id", 1L);

        // --- [수정] Mockito 행동 정의 추가 ---
        // 1. teamRepository.save가 호출되면 team 객체를 반환하도록 설정
        when(teamRepository.save(Mockito.any(Team.class))).thenReturn(team);

        // 2. teamRepository.findById가 1L로 호출되면, Optional<Team>을 반환하도록 설정
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // 3. memberRepository.findMemberBySabun이 호출되면, Optional<Member>를 반환하도록 설정
        when(memberRepository.findBySabunIn(List.of("123456"))).thenReturn(List.of(member));
        // --- ---

        // when
        Team teamRegister = teamService.teamRegister(team, member);

        // then
        assertThat(teamRegister.getId()).isEqualTo(1L);
        assertThat(teamRegister.getTeamName()).isEqualTo(team.getTeamName());
    }

    @Test
    @DisplayName("팀 소속 성공")
    void teamJoinSuccess() {
        // given (준비)
        // 1. 테스트에 사용할 DTO 생성
        MemberTeamJoinDto memberTeamJoinDto = this.generateMemberTeamJoinDto();

        // 2. Mock 객체들의 행동을 미리 정의 (Stubbing)
        Team team = this.generateTeam("testTeam1");
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        Member member1 = this.generateMember("123456", "tester1", "pwtest", "테스터1");
        Member member2 = this.generateMember("123457", "tester2", "pwtest", "테스터2");
        when(memberRepository.findBySabunIn(List.of("123456", "123457"))).thenReturn(List.of(member1, member2));

        // saveAll이 호출되면, 인자로 받은 리스트를 그대로 반환하도록 설정
        // (실제로는 저장 로직 후의 객체를 반환하므로, 여기서는 간단하게 anyList()를 사용)
        when(memberTeamRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));


        // when (실행)
        List<MemberTeam> registeredMemberTeams = teamService.joinTeam(memberTeamJoinDto);


        // then (검증)
        // 1. 예상한 수만큼의 MemberTeam 객체가 생성되었는지 확인
        assertThat(registeredMemberTeams).hasSize(2);

        // 2. 각 MemberTeam 객체의 정보가 올바르게 설정되었는지 확인
        assertThat(registeredMemberTeams.get(0).getId().getTeam()).isEqualTo(1L);
        assertThat(registeredMemberTeams.get(0).getId().getMember()).isEqualTo("123456");

        assertThat(registeredMemberTeams.get(1).getId().getTeam()).isEqualTo(1L);
        assertThat(registeredMemberTeams.get(1).getId().getMember()).isEqualTo("123457");
    }

    @Test
    @DisplayName("팀 소속 실패 - 존재하지 않는 팀")
    void teamJoinNotExistsTeam(){

        // given
        MemberTeamJoinDto memberTeamJoinDto = this.generateMemberTeamJoinDto();

        when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.joinTeam(memberTeamJoinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[팀 조인 실패] 존재하지 않는 팀 번호");
    }

    @Test
    @DisplayName("팀 소속 실패 - 존재하지 않는 사번")
    void teamJoinNotExistsSabun(){

        // given
        MemberTeamJoinDto memberTeamJoinDto = this.generateMemberTeamJoinDto();
        Team team = this.generateTeam("testTeam1");

        when(memberRepository.findBySabunIn(List.of("123456", "123457"))).thenReturn(List.of());
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        // when & then
        assertThatThrownBy(() -> teamService.joinTeam(memberTeamJoinDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("[팀 조인 실패] 존재하지 않는 사원");
    }
}