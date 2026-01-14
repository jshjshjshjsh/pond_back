package com.itjamz.pond_back.k6.infra.repository;

import com.itjamz.pond_back.k6.domain.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PointJpaRepository extends JpaRepository<Point, Long> {

    @Query("select p from Point p where p.memberId = :memberId")
    Optional<Point> findByMemberIdForUpdate(String memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.memberId = :memberId")
    Optional<Point> findByMemberIdWithPessimisticLock (String memberId);
}