package com.itjamz.pond_back.calendar.controller;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
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

    // todo: 요약 공유 데이터 저장

}
