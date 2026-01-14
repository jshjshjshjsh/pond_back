package com.itjamz.pond_back.user.controller;

import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.dto.MemberDto;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<Void> memberRegister(@RequestBody MemberDto member) {
        memberService.memberRegister(member);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<MemberDto> memberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(MemberDto.from(userDetails.getMember()));
    }

    @PatchMapping("/info")
    public ResponseEntity<Void> memberInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MemberDto memberDto) {
        memberService.memberChangeInfo(userDetails.getMember(), memberDto);

        return ResponseEntity.ok().build();
    }
}
