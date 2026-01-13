package com.itjamz.pond_back.k6.service;

import com.itjamz.pond_back.k6.domain.Point;
import com.itjamz.pond_back.k6.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public Long deposit(String memberId, Long amount) {
        Point point = pointRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return point.deposit(amount);
    }

    @Transactional
    public Long withdraw(String memberId, Long amount) {
        Point point = pointRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return point.withdraw(amount);
    }

    @Transactional(readOnly = true)
    public Long getPoint(String memberId) {
        Point point = pointRepository.findByMemberIdForUpdate(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return point.getAmount();
    }
}
