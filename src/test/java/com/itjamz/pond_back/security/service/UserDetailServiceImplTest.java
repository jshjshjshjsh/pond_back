package com.itjamz.pond_back.security.service;

import com.itjamz.pond_back.security.domain.CustomUserDetails;
import com.itjamz.pond_back.user.domain.entity.Member;
import com.itjamz.pond_back.user.domain.entity.MemberRole;
import com.itjamz.pond_back.user.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceImplTest {

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("사용자 정보 로드 성공")
    void loadUserByUsernameSucess(){
        // given
        Member mockMember = Member.builder()
                .id("testuser")
                .pw("encoded_password")
                .role(MemberRole.ROLE_NORMAL)
                .build();
        when(memberService.findMemberById("testuser")).thenReturn(Optional.of(mockMember));

        // when
        UserDetails userDetails = userDetailService.loadUserByUsername("testuser");

        // then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("encoded_password");
        assertThat(userDetails.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_NORMAL"));
        assertThat(((CustomUserDetails) userDetails).getMember()).isEqualTo(mockMember);
    }

    @Test
    @DisplayName("사용자 정보 로드 실패 - 사용자를 찾을 수 없음")
    void loadUserByUsernameFailUserNotFound(){

        // given
        when(memberService.findMemberById("nonexists")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userDetailService.loadUserByUsername("nonexists"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with id: nonexists");
    }
}