package com.itjamz.pond_back.user.infra.repository;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository jpaRepository;

    @Override
    public Member save(Member member) {
        return jpaRepository.save(member);
    }

    @Override
    public Optional<Member> findMemberBySabun(String sabun){
        return jpaRepository.findMemberBySabun(sabun);
    };

    @Override
    public Optional<Member> findMemberById(String id){
        return jpaRepository.findMemberById(id);
    };

    @Override
    public Optional<Member> findMemberByIdOrSabun(String id, String sabun){
        return jpaRepository.findMemberByIdOrSabun(id, sabun);
    };

    @Override
    public List<Member> findTeamMembersByMemberSabun(@Param("sabun") String sabun){
        return jpaRepository.findTeamMembersByMemberSabun(sabun);
    };

    @Override
    public List<Member> findBySabunIn(List<String> sabuns){
        return jpaRepository.findBySabunIn(sabuns);
    };
}
