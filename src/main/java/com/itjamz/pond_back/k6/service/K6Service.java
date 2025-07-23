package com.itjamz.pond_back.k6.service;

import com.itjamz.pond_back.aop.DistributedLock;
import com.itjamz.pond_back.k6.domain.Mileage;
import com.itjamz.pond_back.k6.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class K6Service {

    private final static String DISTRIBUTE_KEY = "mileage_lock";
    private final MileageRepository mileageRepository;

    /**
     * 실제 입금 작업을 수행하는 트랜잭션 메서드
     */
    @DistributedLock(key = DISTRIBUTE_KEY + ":{id}")
    @Transactional
    public Long deposit(String id, Long amount) {
        Mileage mileage = mileageRepository.findByMember_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return mileage.deposit(amount);
    }

    /**
     * 실제 출금 작업을 수행하는 트랜잭션 메서드
     */
    @DistributedLock(key = DISTRIBUTE_KEY + ":{id}")
    @Transactional
    public Long withdraw(String id, Long amount) {
        Mileage mileage = mileageRepository.findByMember_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return mileage.withdraw(amount);
    }

    /**
     * 마일리지 조회를 위한 메서드
     */
    @Transactional(readOnly = true)
    public Long getMileage(String id) {
        Mileage mileage = mileageRepository.findByMember_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return mileage.getAmount();
    }
}