package com.itjamz.pond_back.user.repository;

import com.itjamz.pond_back.user.domain.entity.MemberTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberTeamRepository extends JpaRepository<MemberTeam, Long> {

}
