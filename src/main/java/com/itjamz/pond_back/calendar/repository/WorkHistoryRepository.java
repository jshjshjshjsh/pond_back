package com.itjamz.pond_back.calendar.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long> {
}
