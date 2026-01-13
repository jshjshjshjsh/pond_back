package com.itjamz.pond_back.k6.infra.aop;

import com.itjamz.pond_back.common.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class RetryAspect {

    @Around("@annotation(retry)")
    public Object retry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {

        int retryValue = retry.value();
        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= retryValue; retryCount++) {
            try {
                return joinPoint.proceed();

            } catch (OptimisticLockingFailureException e) {
                exceptionHolder = e;
                log.warn("낙관적 락 충돌 발생! 재시도 {}/{}", retryCount, retryValue);

                // DB 부하 줄이기 위해 잠깐 대기
                Thread.sleep(50);
            }
        }

        // 횟수 다 썼는데도 실패하면 에러 던짐
        throw exceptionHolder;
    }
}
