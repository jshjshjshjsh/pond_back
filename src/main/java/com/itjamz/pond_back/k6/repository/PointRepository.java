package com.itjamz.pond_back.k6.repository;

import com.itjamz.pond_back.k6.domain.Point;

import java.util.Optional;

public interface PointRepository {

    Point save(Point point);
    Optional<Point> findByMemberIdForUpdate(String memberId);
    Optional<Point> findByMemberIdWithPessimisticLock(String memberId);
}
