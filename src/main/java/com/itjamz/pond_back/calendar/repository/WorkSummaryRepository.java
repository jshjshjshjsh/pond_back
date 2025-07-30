package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkSummaryRepository extends JpaRepository<WorkSummary, Long> {

    List<WorkSummary> findByYearAndMonthAndMember_Sabun(int year, int month, String sabun);

    @Query("SELECT DISTINCT ws FROM WorkSummary ws " +
            "JOIN MemberTeam mt " +
            "ON ws.member = mt.member " +
            "WHERE mt.team in (SELECT mt2 FROM MemberTeam mt2 WHERE mt2.member.sabun = :memberSabun) " +
            "AND ws.year = :year AND ws.month = :month " +
            "ORDER BY ws.member.sabun ")
    List<WorkSummary> findTeamWorkSummaryByYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("memberSabun") String memberSabun);
}
