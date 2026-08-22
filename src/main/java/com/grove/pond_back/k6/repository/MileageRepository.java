package com.grove.pond_back.k6.repository;

import com.grove.pond_back.k6.domain.Mileage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MileageRepository extends JpaRepository<Mileage, Long> {

    Optional<Mileage> findByMember_Id(String id);
}
