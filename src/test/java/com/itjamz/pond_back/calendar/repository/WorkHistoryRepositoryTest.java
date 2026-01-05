package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.domain.entity.WorkRecordDate;
import com.itjamz.pond_back.user.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
class WorkHistoryRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    WorkHistoryRepository workHistoryRepository;

    @Test
    void findWorkHistoriesByBetweenSearchDate() {
        // given
        Member member = Member.builder()
                .sabun("123456")
                .id("tester")
                .pw(new MemberPw("pwpwpw"))
                .name("test")
                .role(MemberRole.ROLE_LEADER)
                .build();

        Team team1 = Team.builder()
                .teamName("TEAM1")
                .build();

        WorkHistory test1 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 1, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 2, 0, 0, 0))
                                .build()
                )
                .title("test1")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test2 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 3, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 8, 0, 0, 0))
                                .build()
                )
                .title("test2")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test3 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 10, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 19, 0, 0, 0))
                                .build()
                )
                .title("test3")
                .member(member)
                .team(team1)
                .build();

        WorkHistory test4 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 20, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.MAY, 2, 0, 0, 0))
                                .build()
                )
                .title("test4")
                .member(member)
                .team(team1)
                .build();

        entityManager.persistAndFlush(member);
        entityManager.persistAndFlush(team1);
        entityManager.persistAndFlush(test1);
        entityManager.persistAndFlush(test2);
        entityManager.persistAndFlush(test3);
        entityManager.persistAndFlush(test4);

        LocalDateTime startDate = LocalDateTime.of(2025, Month.APRIL, 4, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, Month.APRIL, 23, 0, 0, 0);

        List<WorkHistory> workHistoryList = workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate, endDate, member.getSabun());

        assertThat(workHistoryList.size()).isEqualTo(3);
        assertThat(workHistoryList.get(0).getTitle()).isEqualTo("test2");
        assertThat(workHistoryList.get(1).getTitle()).isEqualTo("test3");
        assertThat(workHistoryList.get(2).getTitle()).isEqualTo("test4");
    }

    @Test
    void findWorkHistoriesByBetweenSearchDateAndMyTeamsOrderByStartDate() {
        // given
        Member member1 = Member.builder()
                .sabun("123456")
                .id("tester1")
                .pw(new MemberPw("pwpwpw"))
                .name("test1")
                .role(MemberRole.ROLE_LEADER)
                .build();

        Member member2 = Member.builder()
                .sabun("123457")
                .id("tester2")
                .pw(new MemberPw("pwpwpw"))
                .name("test2")
                .role(MemberRole.ROLE_NORMAL)
                .build();

        Team team1 = Team.builder()
                .teamName("TEAM1")
                .build();

        member1 = entityManager.persistAndFlush(member1);
        member2 = entityManager.persistAndFlush(member2);
        Team team = entityManager.persistAndFlush(team1);

        MemberTeamId memberTeamId1 = new MemberTeamId(member1.getSabun(), team.getId());
        MemberTeam memberTeam1 = MemberTeam.builder()
                .id(memberTeamId1)
                .member(member1)
                .team(team)
                .build();

        MemberTeamId memberTeamId2 = new MemberTeamId(member2.getSabun(), team.getId());
        MemberTeam memberTeam2 = MemberTeam.builder()
                .id(memberTeamId2)
                .member(member2)
                .team(team)
                .build();

        entityManager.persistAndFlush(memberTeam1);
        entityManager.persistAndFlush(memberTeam2);

        WorkHistory test1 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 1, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 2, 0, 0, 0))
                                .build()
                )
                .title("test1")
                .member(member1)
                .team(team)
                .build();

        WorkHistory test2 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 3, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 8, 0, 0, 0))
                                .build()
                )
                .title("test2")
                .member(member1)
                .team(team)
                .build();

        WorkHistory test3 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 10, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.APRIL, 19, 0, 0, 0))
                                .build()
                )
                .title("test3")
                .member(member2)
                .team(team)
                .build();

        WorkHistory test4 = WorkHistory.builder()
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(LocalDateTime.of(2025, Month.APRIL, 20, 0, 0, 0))
                                .endDate(LocalDateTime.of(2025, Month.MAY, 2, 0, 0, 0))
                                .build()
                )
                .title("test4")
                .member(member2)
                .team(team)
                .build();

        entityManager.persistAndFlush(test1);
        entityManager.persistAndFlush(test2);
        entityManager.persistAndFlush(test3);
        entityManager.persistAndFlush(test4);

        LocalDateTime startDate = LocalDateTime.of(2025, Month.APRIL, 4, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, Month.APRIL, 23, 0, 0, 0);

        List<WorkHistory> workHistoryList = workHistoryRepository.findWorkHistoriesByBetweenSearchDateAndMyTeams(startDate, endDate, member1.getSabun());

        assertThat(workHistoryList.size()).isEqualTo(3);
        assertThat(workHistoryList.get(0).getTitle()).isEqualTo("test2");
        assertThat(workHistoryList.get(1).getTitle()).isEqualTo("test3");
        assertThat(workHistoryList.get(2).getTitle()).isEqualTo("test4");
    }
}