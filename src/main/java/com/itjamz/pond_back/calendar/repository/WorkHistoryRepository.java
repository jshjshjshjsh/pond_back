package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long> {


    @Query("SELECT DISTINCT wh FROM WorkHistory wh " +
            "LEFT JOIN FETCH wh.member m " +
            "LEFT JOIN FETCH m.memberTeams mt " +
            "LEFT JOIN FETCH mt.team " +
            "WHERE wh.member.sabun = :memberSabun " +
            "AND wh.endDate >= :startDate AND wh.startDate <= :endDate " +
            "ORDER BY wh.startDate")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);

    @Query("SELECT DISTINCT wh FROM WorkHistory wh " +
            "LEFT JOIN FETCH wh.member m " +
            "LEFT JOIN FETCH m.memberTeams mt " +
            "LEFT JOIN FETCH mt.team t " +
            "WHERE t IN (SELECT sub_mt.team FROM MemberTeam sub_mt WHERE sub_mt.member.sabun = :memberSabun) " +
            "AND wh.endDate >= :startDate AND wh.startDate <= :endDate " +
            "ORDER BY m.sabun, wh.startDate")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDateAndMyTeams(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);
}
