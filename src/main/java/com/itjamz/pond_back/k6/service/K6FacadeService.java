package com.itjamz.pond_back.k6.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class K6FacadeService {

    private final RedissonClient redissonClient;
    private final K6Service k6Service;

    public Long deposit(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("현재 다른 요청을 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
            return k6Service.performDeposit(id, amount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락을 획득하는 동안 문제가 발생했습니다.", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public Long withdraw(String id, Long amount) {
        RLock lock = redissonClient.getLock("mileage_lock:" + id);
        try {
            boolean isLocked = lock.tryLock(10, 5, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalStateException("현재 다른 요청을 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
            return k6Service.performWithdraw(id, amount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락을 획득하는 동안 문제가 발생했습니다.", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}