package com.itjamz.pond_back.k6.service;

import com.itjamz.pond_back.k6.domain.Mileage;
import com.itjamz.pond_back.k6.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // 1. @Lazy 임포트
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class K6Service {

    private final MileageRepository mileageRepository;
    private final RedissonClient redissonClient;

    private K6Service self;

    // 2. 여기에 @Lazy 어노테이션을 추가하여 순환 참조 문제를 해결합니다.
    @Autowired
    @Lazy
    public void setSelf(K6Service self) {
        this.self = self;
    }

    /**
     * 입금을 처리하는 공개 메서드 (락 제어 담당)
     */
    public Long depositWithLock(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        lock.lock();
        try {
            return self.performDeposit(id, amount);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 실제 입금 작업을 수행하는 내부 메서드 (트랜잭션 담당)
     */
    @Transactional
    public Long performDeposit(String id, Long amount) {
        Mileage mileage = mileageRepository.findByMember_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return mileage.deposit(amount);
    }

    /**
     * 출금을 처리하는 공개 메서드 (락 제어 담당)
     */
    public Long withdrawWithLock(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        lock.lock();
        try {
            return self.performWithdraw(id, amount);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 실제 출금 작업을 수행하는 내부 메서드 (트랜잭션 담당)
     */
    @Transactional
    public Long performWithdraw(String id, Long amount) {
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