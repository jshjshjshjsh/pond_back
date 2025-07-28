package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkSummaryRepository extends JpaRepository<WorkSummary, Long> {

    List<WorkSummary> findByYearAndMonthAndMember_Sabun(int year, int month, String sabun);
}
