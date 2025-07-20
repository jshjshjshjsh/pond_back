package com.itjamz.pond_back.k6.service;

import com.itjamz.pond_back.k6.domain.Mileage;
import com.itjamz.pond_back.k6.repository.MileageRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class K6Service {

    private final MileageRepository mileageRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public Long mileageDeposit(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        try {
            lock.lock(); // 락을 획득할 때까지 대기
            Mileage mileage = mileageRepository.findByMember_Id(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            return mileage.deposit(amount);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public Long mileageWithdraw(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        try {
            lock.lock(); // 락을 획득할 때까지 대기
            Mileage mileage = mileageRepository.findByMember_Id(id)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found"));
            return mileage.withdraw(amount);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(readOnly = true)
    public Long getMileage(String id) {
        Mileage mileage = mileageRepository.findByMember_Id(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return mileage.getAmount();
    }
}
