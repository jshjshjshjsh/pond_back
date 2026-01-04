package com.itjamz.pond_back.global.config;

import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@TestConfiguration
public class TestRedisConfig {

    // 1. 스프링 데이터 Redis가 연결 시도하지 않게 가짜 팩토리 주입
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    // 2. Redisson이 연결 시도하지 않게 가짜 클라이언트 주입
    @Bean
    public RedissonClient redissonClient() {
        return Mockito.mock(RedissonClient.class);
    }
}