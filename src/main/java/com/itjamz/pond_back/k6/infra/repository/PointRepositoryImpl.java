package com.itjamz.pond_back.k6.infra.repository;

import com.itjamz.pond_back.k6.domain.Point;
import com.itjamz.pond_back.k6.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointRepository;

    @Override
    public Optional<Point> findByMemberIdForUpdate(String memberId) {
        return pointRepository.findByMemberIdForUpdate(memberId);
    }
}
