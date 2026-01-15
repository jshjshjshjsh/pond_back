package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.dto.WorkSummaryDto;
import com.itjamz.pond_back.calendar.domain.entity.Work;
import com.itjamz.pond_back.calendar.domain.entity.WorkHistory;
import com.itjamz.pond_back.calendar.domain.entity.WorkRecordDate;
import com.itjamz.pond_back.calendar.domain.entity.WorkSummary;
import com.itjamz.pond_back.calendar.domain.event.WorkSummarySharedEvent;
import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import com.itjamz.pond_back.calendar.repository.WorkSummaryRepository;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Team;
import com.itjamz.pond_back.user.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final TeamRepository teamRepository;
    private final WorkHistoryRepository workHistoryRepository;
    private final WorkSummaryRepository workSummaryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void deleteWorkHistory(Long id, Member member) {
        Optional<WorkHistory> findWorkHistory = workHistoryRepository.findById(id);
        validWork(findWorkHistory, member);

        workHistoryRepository.delete(findWorkHistory.get());
    }

    @Transactional
    public void updateWorkHistory(Long id, WorkHistoryDto workHistoryDto, Member member) {
        // 먼저 본인의 정상적인 workhistory인지 확인
        Optional<WorkHistory> findWorkHistory = workHistoryRepository.findById(id);
        validWork(findWorkHistory, member);

        // 다음 if else 문으로 update
        // team은 find를 해주고 넣어줘야 함
        Team team = null;
        if (workHistoryDto.getTeam() != null)
            team = teamRepository.findById(workHistoryDto.getTeam().getId()).orElse(null);
        findWorkHistory.get().patchWorkHistory(workHistoryDto, team);
    }

    @Transactional
    public WorkHistory saveWorkHistory(WorkHistoryDto workHistoryDto, Member member) {
        Team team = null;
        if (workHistoryDto.getTeam() != null && workHistoryDto.getTeam().getId() != null) {
            team = teamRepository.findById(workHistoryDto.getTeam().getId()).orElse(null);
        }

        WorkHistory workHistory = WorkHistory.builder()
                .title(workHistoryDto.getTitle())
                .content(workHistoryDto.getContent())
                .workRecordDate(
                        WorkRecordDate.builder()
                                .startDate(workHistoryDto.getStartDate())
                                .endDate(workHistoryDto.getEndDate())
                                .build()
                )
                .isShare(workHistoryDto.getIsShare())
                .member(member)
                .team(team)
                .build();


        return workHistoryRepository.save(workHistory);
    }

    @Transactional(readOnly = true)
    public List<WorkHistoryDto> findWorkHistoryByDate(LocalDate startDate, LocalDate endDate, Member member) {
        List<WorkHistory> workHistories = workHistoryRepository.findWorkHistoriesByBetweenSearchDate(startDate.atStartOfDay(), endDate.atStartOfDay(), member.getSabun());
        return workHistories.stream().map(WorkHistoryDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkSummaryDto> findWorkSummaryPersonal(int year, int month, Member member){

        List<WorkSummary> workSummaries = workSummaryRepository.findByYearAndMonthAndMember_Sabun(year, month, member.getSabun());
        return workSummaries.stream().map(WorkSummaryDto::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkSummaryDto> findTeamWorkSummary(int year, int month, Member member){

        List<WorkSummary> workSummaries = workSummaryRepository.findTeamWorkSummaryByYearAndMonth(year, month, member.getSabun());
        return workSummaries.stream().map(WorkSummaryDto::from).collect(Collectors.toList());
    }

    @Transactional
    public WorkSummary saveWorkSummary(WorkSummaryDto workSummaryDto, Member member){
        WorkSummary workSummary = WorkSummary.builder()
                .year(workSummaryDto.getYear())
                .month(workSummaryDto.getMonth())
                .isShare(workSummaryDto.getIsShare())
                .summary(workSummaryDto.getSummary())
                .member(member)
                .build();

        WorkSummary savedWorkSummary = workSummaryRepository.save(workSummary);

        // 만약 공유 상태로 Update 될 시 팀원들에게 알림 전송(알림 구현은 안되어있음)
        if (Boolean.TRUE.equals(savedWorkSummary.getIsShare())) {
            eventPublisher.publishEvent(new WorkSummarySharedEvent(
                    savedWorkSummary.getId(),
                    member.getName(),
                    savedWorkSummary.getYear(),
                    savedWorkSummary.getMonth()
            ));
        }

        return savedWorkSummary;
    }

    @Transactional
    public void deleteWorkSummary(Long id, Member member) {
        // 본인의 worksummary 인지 검증
        Optional<WorkSummary> workSummary = workSummaryRepository.findById(id);
        validWork(workSummary, member);

        workSummaryRepository.deleteById(id);
    }

    @Transactional
    public WorkSummary updateWorkSummary(WorkSummaryDto workSummaryDto, Member member){
        // 현재는 share 값만 수정 가능
        Optional<WorkSummary> workSummary = workSummaryRepository.findById(workSummaryDto.getId());
        validWork(workSummary, member);

        if (workSummaryDto.getIsShare() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[worksummary] 잘못된 요청");

        workSummary.get().changeIsShare(workSummaryDto.getIsShare());

        return workSummary.get();
    }

    private void validWork(Optional<? extends Work> work, Member member){
        if (work.isEmpty() || !work.get().getMember().getId().equals(member.getId()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[workhistory 조회 실패] 조회 실패");
    }
}
