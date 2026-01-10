package com.itjamz.pond_back.calendar.infra.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkHistoryRepositoryImpl implements WorkHistoryRepository {

    private final WorkHistoryJpaRepository workHistoryJpaRepository;

    @Override
    public WorkHistory save(WorkHistory workHistory) {
        return workHistoryJpaRepository.save(workHistory);
    }

    @Override
    public void delete(WorkHistory workHistory) {
        workHistoryJpaRepository.delete(workHistory);
    }

    @Override
    public Optional<WorkHistory> findById(Long id) {
        return workHistoryJpaRepository.findById(id);
    }

    @Override
    public List<WorkHistory> findWorkHistoriesByBetweenSearchDate(LocalDateTime startDate, LocalDateTime endDate, String memberSabun) {
        return workHistoryJpaRepository.findWorkHistoriesByBetweenSearchDate(startDate, endDate, memberSabun);
    }

    @Override
    public List<WorkHistory> findWorkHistoriesByBetweenSearchDateAndMyTeams(LocalDateTime startDate, LocalDateTime endDate, String memberSabun) {
        return workHistoryJpaRepository.findWorkHistoriesByBetweenSearchDateAndMyTeams(startDate, endDate, memberSabun);
    }
}
