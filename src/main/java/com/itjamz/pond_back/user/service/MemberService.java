package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.user.entity.Member;
import com.itjamz.pond_back.user.entity.Member_Role;
import com.itjamz.pond_back.user.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional
    public void memberRegister(Member member) {
        if (memberRepository.findById(member.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[회원가입 실패] 이미 존재하는 사번");
        }

        String encodedPw = passwordEncoder.encode(member.getPw());
        member.initRegister(encodedPw, Member_Role.ROLE_NORMAL);

        memberRepository.save(member);
    }
}

