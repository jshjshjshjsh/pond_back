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
            "    JOIN MemberTeam mt_wh ON wh.member.sabun = mt_wh.member.sabun " +
            "    JOIN MemberTeam mt_target ON mt_wh.team.id = mt_target.team.id " +
            "    WHERE mt_target.member.sabun = :memberSabun" +
            "    AND wh.endDate >= :startDate AND wh.startDate <= :endDate ")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, String memberSabun);
}
