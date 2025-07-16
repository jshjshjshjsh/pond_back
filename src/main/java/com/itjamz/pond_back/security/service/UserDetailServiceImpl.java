package com.itjamz.pond_back.security.service;

import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        Member member = memberService.findMemberById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        // Member 정보를 담은 CustomUserDetails 객체를 반환
        return new CustomUserDetails(member);

        /*
        Optional<Member> memberOptional = memberService.findMemberById(id);

        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return new User(member.getId(), member.getPw(), Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
        } else {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }

         */
    }
}
