package com.itjamz.pond_back.k6.controller;

import com.itjamz.pond_back.k6.service.K6Service;
import com.itjamz.pond_back.security.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/k6")
public class K6Controller {

    private final K6Service k6Service;

    @PostMapping("/deposit")
    public ResponseEntity<Long> mileageDeposit(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails){
        // 서비스의 depositWithLock 메서드를 호출하도록 변경
        return ResponseEntity.ok(k6Service.depositWithLock(userDetails.getMember().getId(), amount));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Long> mileageWithdraw(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails){
        // 서비스의 withdrawWithLock 메서드를 호출하도록 변경
        return ResponseEntity.ok(k6Service.withdrawWithLock(userDetails.getMember().getId(), amount));
    }

    @GetMapping("/mileage")
    public ResponseEntity<Long> getMileage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(k6Service.getMileage(userDetails.getMember().getId()));
    }
}