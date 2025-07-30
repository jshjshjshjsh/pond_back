package com.itjamz.pond_back.calendar.controller;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.domain.dto.WorkSummaryDto;
import com.itjamz.pond_back.calendar.service.CalendarService;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @DeleteMapping("/workhistory/{id}")
    public ResponseEntity<Void> deleteWorkHistory(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.deleteWorkHistory(id, userDetails.getMember());

        return ResponseEntity.ok().build();
    }


    @PatchMapping("/workhistory/{id}")
    public ResponseEntity<Void> updateWorkHistory(@PathVariable Long id, @RequestBody WorkHistoryDto workHistoryDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.updateWorkHistory(id, workHistoryDto, userDetails.getMember());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/workhistory/save")
    public ResponseEntity<Void> saveWorkHistory(@RequestBody WorkHistoryDto workHistoryDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.saveWorkHistory(workHistoryDto, userDetails.getMember());

        return ResponseEntity.ok().build();
    }

    /**
     * /workhistory/list: 본인의 workhistory 리스트 조회
     *                    (startDate, endDate) 기준으로 사이에 들어있는 것만 가져옴
     *
     * /workhistory/team/list: 특정 팀에 소속된 리스트 조회
     *                         특정 팀이 값으로 들어오면 특정 팀의 값들 조회
     *                         특정 팀이 없으면 본인의 팀으로 조회
     *                         (startDate, endDate) 기준으로 사이에 있는 것만 가져옴
     * */
    @GetMapping("/workhistory/list")
    public ResponseEntity<List<WorkHistoryDto>> workHistoryList(@RequestParam("startDate")LocalDate startDate, @RequestParam("endDate")LocalDate endDate,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails){

        return ResponseEntity.ok(calendarService.findWorkHistoryByDate(startDate, endDate, userDetails.getMember()));
    }

    /**
     * 본인 요약 데이터 저장(o), 조회, 삭제(o), 수정(공유 유무)
     * 요약 데이터는 월별로 라벨을 넣고 공유하고 가져감
     * 조회는 본인의 월별을 조회하는 /calendar/worksummary/list
     *       본인 팀원의 월별 조회인 /calendar/leader/worksummary/list 가 있음
     */

    @GetMapping("/worksummary/list")
    public ResponseEntity<List<WorkSummaryDto>> getMyWorkSummary(@RequestParam int year, @RequestParam int month,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){

        return ResponseEntity.ok(calendarService.findWorkSummaryPersonal(year, month, userDetails.getMember()));
    }

    @GetMapping("/leader/worksummary/list")
    public ResponseEntity<List<WorkSummaryDto>> getTeamWorkSummary(@RequestParam int year, @RequestParam int month,
                                                                 @AuthenticationPrincipal CustomUserDetails userDetails){

        return ResponseEntity.ok(calendarService.findTeamWorkSummary(year, month, userDetails.getMember()));
    }

    @PostMapping("/worksummary")
    public ResponseEntity<Void> saveWorkSummary(@RequestBody WorkSummaryDto workSummaryDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        calendarService.saveWorkSummary(workSummaryDto, userDetails.getMember());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/worksummary/{id}")
    public ResponseEntity<Void> deleteWorkSummary(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.deleteWorkSummary(id, userDetails.getMember());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/worksummary")
    public ResponseEntity<Void> updateWorkSummary(@RequestBody WorkSummaryDto workSummaryDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.updateWorkSummary(workSummaryDto, userDetails.getMember());

        return ResponseEntity.ok().build();
    }
}
