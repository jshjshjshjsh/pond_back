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
            "JOIN FETCH wh.member m " +
            "JOIN FETCH wh.team t " +
            "WHERE m.sabun = :memberSabun " +
            "AND wh.workRecordDate.endDate >= :startDate " +
            "AND wh.workRecordDate.startDate <= :endDate " +
            "ORDER BY wh.workRecordDate.startDate")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);

    @Query("SELECT DISTINCT wh FROM WorkHistory wh " +
            "JOIN FETCH wh.member m " +
            "JOIN MemberTeam mt ON m.sabun = mt.id.member " + // 객체 참조 대신 필드 값으로 직접 조인
            "WHERE mt.id.team IN (" +
            "    SELECT sub_mt.id.team FROM MemberTeam sub_mt " +
            "    WHERE sub_mt.id.member = :memberSabun" +
            ") " +
            "AND wh.workRecordDate.endDate >= :startDate " +
            "AND wh.workRecordDate.startDate <= :endDate " +
            "ORDER BY m.sabun, wh.workRecordDate.startDate")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDateAndMyTeams(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);
}
