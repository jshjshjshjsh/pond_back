package com.itjamz.pond_back.k6.infra.repository;

import com.itjamz.pond_back.k6.domain.Point;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PointJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Test
    void findByMemberIdForUpdate() {

        //given
        String memberId = "tester";
        Point point = Point.builder()
                .amount(100L)
                .memberId(memberId)
                .version(1L)
                .build();

        entityManager.persistAndFlush(point);

        //when
        Optional<Point> findPoint = pointJpaRepository.findByMemberIdForUpdate(memberId);

        //then
        assertThat(findPoint).isPresent();
        assertThat(findPoint.get().getId()).isEqualTo(point.getId());
        assertThat(findPoint.get().getAmount()).isEqualTo(point.getAmount());
    }
}