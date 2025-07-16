package com.itjamz.pond_back.calendar.controller;

import com.itjamz.pond_back.calendar.domain.dto.WorkHistoryDto;
import com.itjamz.pond_back.calendar.service.CalendarService;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping("/workhistory/save")
    public ResponseEntity<?> saveWorkHistory(@RequestBody WorkHistoryDto workHistoryDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        calendarService.saveWorkHistory(workHistoryDto, userDetails.getMember());

        return ResponseEntity.ok().build();
    }
}
