package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long> {


    @Query(" SELECT DISTINCT wh FROM WorkHistory wh " +
            "    WHERE wh.member.sabun = :memberSabun " +
            "    AND wh.endDate >= :startDate AND wh.startDate <= :endDate " +
            "    ORDER BY wh.startDate ")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, String memberSabun);

    @Query("SELECT DISTINCT wh FROM WorkHistory wh " +
            "WHERE wh.team IN (SELECT mt.team FROM MemberTeam mt WHERE mt.member.sabun = :memberSabun) " +
            "AND wh.endDate >= :startDate AND wh.startDate <= :endDate " +
            "ORDER BY wh.member.sabun, wh.startDate")
    // todo: 팀 단위로 검색해야함
    List<WorkHistory> findWorkHistoriesByBetweenSearchDateAndMyTeams(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);
}
