package com.itjamz.pond_back.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@RequiredArgsConstructor
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.itjamz.pond_back.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 동적인 락 키 생성을 위해 파라미터 값을 조합
        String key = createDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock lock = redissonClient.getLock(key);

        try {
            // 설정된 시간만큼 락 획득을 시도
            boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!isLocked) {
                throw new IllegalStateException("락 획득에 실패했습니다.");
            }
            // 락을 획득한 상태에서 별도의 트랜잭션으로 원본 메서드를 호출
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException("락 획득 중 인터럽트 발생");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 파라미터를 조합하여 동적인 키를 생성하는 헬퍼 메서드
    private String createDynamicKey(String[] parameterNames, Object[] args, String key) {
        // 예: key가 "user-lock:#userId" 이고, 메서드 파라미터로 userId=123이 들어오면 -> "user-lock:123"으로 변환
        for (int i = 0; i < parameterNames.length; i++) {
            key = key.replace(":" + parameterNames[i], args[i].toString());
        }
        return key;
    }
}
