package com.itjamz.pond_back.k6.controller;

import com.itjamz.pond_back.k6.service.K6FacadeService; // K6Service 대신 Facade를 import
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

    private final K6FacadeService k6FacadeService; // Facade Service를 주입받습니다.
    private final K6Service k6Service; // getMileage는 락이 필요 없으므로 직접 호출

    @PostMapping("/deposit")
    public ResponseEntity<Long> mileageDeposit(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails){
        // Facade Service의 입금 메서드를 호출합니다.
        return ResponseEntity.ok(k6FacadeService.deposit(userDetails.getMember().getId(), amount));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Long> mileageWithdraw(@RequestBody Long amount, @AuthenticationPrincipal CustomUserDetails userDetails){
        // Facade Service의 출금 메서드를 호출합니다.
        return ResponseEntity.ok(k6FacadeService.withdraw(userDetails.getMember().getId(), amount));
    }

    @GetMapping("/mileage")
    public ResponseEntity<Long> getMileage(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 조회는 락이 필요 없으므로 기존 K6Service를 그대로 사용합니다.
        return ResponseEntity.ok(k6Service.getMileage(userDetails.getMember().getId()));
    }
}