package com.itjamz.pond_back;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DisplayName("통합 테스트")
@Testcontainers
public abstract class AbstractContainerBaseTest {

    private static final String REDIS_IMAGE = "redis:latest";

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER =
            new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
                    .withExposedPorts(6379) // 컨테이너의 6379 포트를 외부에 노출
                    .withReuse(true); // 테스트 실행 간 컨테이너 재사용

    // Spring 컨텍스트가 로드되기 전에 동적으로 Redis 연결 정보를 설정합니다.
    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> String.valueOf(REDIS_CONTAINER.getMappedPort(6379)));
    }
}