package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.Member_Role;
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

    @Transactional
    public Member memberRegister(Member member) {
        if (memberRepository.findMemberBySabun(member.getSabun()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[회원가입 실패] 이미 존재하는 사번");
        }

        String encodedPw = passwordEncoder.encode(member.getPw());
        member.encodedPw(encodedPw);

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(String id) {
        return memberRepository.findMemberById(id);
    }
}

