package com.grove.pond_back.aop;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@RequiredArgsConstructor
@Order(1)
public class DistributedLockAop {

    private final RedissonClient redissonClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Around("@annotation(com.grove.pond_back.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        // 동적인 락 키 생성을 위해 파라미터 값을 조합
        String key = createDynamicKey(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
        RLock lock = redissonClient.getLock(key);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("redisLock");
        boolean isLocked;

        try {
            // 🔥 오직 '락 획득' 과정만 서킷 브레이커로 감싸서 네트워크 지연/장애를 측정함
            isLocked = circuitBreaker.executeCallable(() ->
                    lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit())
            );
        } catch (CallNotPermittedException e) {
            // 서킷 브레이커가 OPEN 상태일 때 빠르게 실패(Fail-fast) 처리
            throw new IllegalStateException("현재 요청이 많아 처리할 수 없습니다.");
        } catch (Exception e) {
            // Redis 통신 장애 등
            throw new InterruptedException("락 획득 중 인터럽트/장애 발생");
        }

        if (!isLocked) {
            throw new IllegalStateException("락 획득에 실패했습니다.");
        }

        try {
            // 락을 획득한 상태에서 별도의 트랜잭션으로 원본 메서드를 호출 (여기는 서킷 측정 대상에서 제외!)
            return joinPoint.proceed();
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
