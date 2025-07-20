package com.itjamz.pond_back.k6.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class K6FacadeService {

    private final RedissonClient redissonClient;
    private final K6Service k6Service; // 실제 비즈니스 로직을 처리할 서비스

    /**
     * 입금을 처리하는 파사드 메서드
     */
    public Long deposit(String id, Long amount) {
        // 1. 사용자 ID 기반으로 락 객체를 가져옵니다.
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        lock.lock(); // 2. 락을 획득합니다.
        try {
            // 3. 락을 획득한 상태에서 실제 DB 작업을 수행하는 트랜잭션 서비스를 호출합니다.
            return k6Service.performDeposit(id, amount);
        } finally {
            // 4. 서비스 로직이 완전히 끝난 후, 락을 해제합니다.
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 출금을 처리하는 파사드 메서드
     */
    public Long withdraw(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        lock.lock();
        try {
            return k6Service.performWithdraw(id, amount);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}