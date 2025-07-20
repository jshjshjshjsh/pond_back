package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.k6.domain.Mileage;
import com.itjamz.pond_back.k6.repository.MileageRepository;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MileageRepository mileageRepository;

    @Transactional
    public Member memberRegister(Member member) {
        if (memberRepository.findMemberByIdOrSabun(member.getId(), member.getSabun()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "[회원가입 실패] 이미 존재하는 ID 또는 사번");
        }

        String encodedPw = passwordEncoder.encode(member.getPw());
        member.encodedPw(encodedPw);

        Member savedMember = memberRepository.save(member);

        Mileage mileage = Mileage.builder()
                .member(savedMember)
                .amount(0L)
                .build();
        mileageRepository.save(mileage);

        return savedMember;
    }

    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(String id) {
        return memberRepository.findMemberById(id);
    }
}


