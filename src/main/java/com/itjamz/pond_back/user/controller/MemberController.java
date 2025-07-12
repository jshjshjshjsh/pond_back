package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.user.entity.Member;
import com.itjamz.pond_back.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member/register")
    public ResponseEntity<?> userRegister(Member member) {
        memberService.memberRegister(member);

        return ResponseEntity.ok().build();
    }
}
