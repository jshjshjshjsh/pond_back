package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkSummaryRepository {

    WorkSummary save(WorkSummary workSummary);
    void delete(WorkSummary workSummary);
    void deleteById(Long id);
    Optional<WorkSummary> findById(Long id);
    List<WorkSummary> findByYearAndMonthAndMember_Sabun(int year, int month, String sabun);
    List<WorkSummary> findTeamWorkSummaryByYearAndMonth(@Param("year") int year, @Param("month") int month, @Param("memberSabun") String memberSabun);

}
