package com.itjamz.pond_back.k6.controller;

import com.itjamz.pond_back.k6.service.PointService;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/k6/point")
public class PointController {

    private final PointService pointService;

    @PostMapping("/deposit")
    public ResponseEntity<Long> deposit(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(pointService.deposit(userDetails.getMember().getId(), amount));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Long> withdraw(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(pointService.withdraw(userDetails.getMember().getId(), amount));
    }

    @GetMapping("/point")
    public ResponseEntity<Long> getPoint(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(pointService.getPoint(userDetails.getMember().getId()));
    }
}
