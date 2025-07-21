package com.itjamz.pond_back.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {


    /** 락의 이름 (Key) */
    String key();

    /** 락의 시간 단위 (기본값: 초) */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /** 락을 얻기 위해 기다리는 시간 (기본값: 5초) */
    long waitTime() default 5L;

    /** 락이 자동으로 해제되기까지의 시간 (기본값: 3초) */
    long leaseTime() default 3L;
}
