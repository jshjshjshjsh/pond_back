package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/register")
    public ResponseEntity<?> memberRegister(@RequestBody Member member) {
        memberService.memberRegister(member);

        return ResponseEntity.ok().build();
    }
}
