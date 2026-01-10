package com.itjamz.pond_back.calendar.infra.repository;

import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import com.itjamz.pond_back.calendar.repository.WorkSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WorkSummaryRepositoryImpl implements WorkSummaryRepository {

    private final WorkSummaryJpaRepository workSummaryJpaRepository;

    @Override
    public WorkSummary save(WorkSummary workSummary) {
        return workSummaryJpaRepository.save(workSummary);
    }

    @Override
    public void delete(WorkSummary workSummary) {
        workSummaryJpaRepository.delete(workSummary);
    }

    @Override
    public void deleteById(Long id) {
        workSummaryJpaRepository.deleteById(id);
    }

    @Override
    public Optional<WorkSummary> findById(Long id) {
        return workSummaryJpaRepository.findById(id);
    }

    @Override
    public List<WorkSummary> findByYearAndMonthAndMember_Sabun(int year, int month, String sabun) {
        return workSummaryJpaRepository.findByYearAndMonthAndMember_Sabun(year, month, sabun);
    }

    @Override
    public List<WorkSummary> findTeamWorkSummaryByYearAndMonth(int year, int month, String memberSabun) {
        return workSummaryJpaRepository.findByYearAndMonthAndMember_Sabun(year, month, memberSabun);
    }
}
