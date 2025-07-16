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

    @Query("SELECT w FROM WorkHistory w WHERE w.endDate >= :startDate AND w.startDate <= :endDate")
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
