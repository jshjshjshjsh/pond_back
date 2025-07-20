package com.itjamz.pond_back.k6.repository;

import com.itjamz.pond_back.k6.domain.Mileage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MileageRepository extends JpaRepository<Mileage, Long> {

    Optional<Mileage> findByMember_Id(String id);
}
