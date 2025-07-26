package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Void> memberRegister(@RequestBody Member member) {
        memberService.memberRegister(member);

        return ResponseEntity.ok().build();
    }

    // todo: 본인 정보 가져오는 api
    // todo: role, 비밀번호 변경 api
}
