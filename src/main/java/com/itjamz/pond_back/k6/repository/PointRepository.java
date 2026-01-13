package com.itjamz.pond_back.k6.repository;

import com.itjamz.pond_back.k6.domain.Point;

import java.util.Optional;

public interface PointRepository {

    Optional<Point> findByMemberIdForUpdate(String memberId);
}
