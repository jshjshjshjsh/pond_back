package com.itjamz.pond_back.user.service;

import com.itjamz.pond_back.k6.domain.Mileage;
import com.itjamz.pond_back.k6.domain.Point;
import com.itjamz.pond_back.k6.repository.MileageRepository;
import com.itjamz.pond_back.k6.repository.PointRepository;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberPw;
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
    private final PointRepository pointRepository;

    @Transactional
    public Member memberChangeInfo(Member member, MemberDto memberDto) {
        Optional<Member> findMember = memberRepository.findMemberBySabun(member.getSabun());
        if (findMember.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "[계정 조회 실패] 존재하지 않는 ID 또는 사번");
        }

        if (memberDto.getPw() != null)
            findMember.get().encodedPw(memberDto.getPw(), passwordEncoder);
        findMember.get().changeInfo(memberDto);

        return findMember.get();
    }

    @Transactional
    public Member memberRegister(MemberDto memberDto) {
        if (memberRepository.findMemberByIdOrSabun(memberDto.getId(), memberDto.getSabun()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "[회원가입 실패] 이미 존재하는 ID 또는 사번");
        }

        Member member = Member.builder()
                .sabun(memberDto.getSabun())
                .id(memberDto.getId())
                .pw(new MemberPw(memberDto.getPw()))
                .name(memberDto.getName())
                .role(memberDto.getRole())
                .build();

        System.out.println("member.toString() = " + member.toString());

        member.encodedPw(member.getPw().getPw(), passwordEncoder);

        Member savedMember = memberRepository.save(member);

        Mileage mileage = Mileage.builder()
                .member(savedMember)
                .amount(0L)
                .build();
        mileageRepository.save(mileage);

        Point point = Point.builder()
                .memberId(member.getId())
                .amount(0L)
                .version(0L)
                .build();
        pointRepository.save(point);

        return savedMember;
    }

    @Transactional(readOnly = true)
    public Optional<Member> findMemberById(String id) {
        return memberRepository.findMemberById(id);
    }
}


