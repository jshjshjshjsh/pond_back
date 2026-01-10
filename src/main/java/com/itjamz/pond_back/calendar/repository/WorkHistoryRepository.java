package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkHistoryRepository {

    WorkHistory save(WorkHistory workHistory);
    void delete(WorkHistory workHistory);
    Optional<WorkHistory> findById(Long id);
    List<WorkHistory> findWorkHistoriesByBetweenSearchDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);
    List<WorkHistory> findWorkHistoriesByBetweenSearchDateAndMyTeams(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("memberSabun") String memberSabun);

}
