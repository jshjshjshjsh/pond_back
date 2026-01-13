package com.itjamz.pond_back.k6.service;

import com.itjamz.pond_back.k6.domain.Point;
import com.itjamz.pond_back.k6.repository.PointRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    private Point pointGeneratorWithAmount(Long amount){
        return Point.builder()
                .id(1L)
                .amount(amount)
                .memberId("tester")
                .version(1L)
                .build();
    }

    @Test
    @DisplayName("입금 로직 테스트")
    void depositOptimistic() {
        //given
        String memberId = "tester";
        Long amount = 100L;

        //when
        when(pointRepository.findByMemberIdForUpdate(memberId)).thenReturn(Optional.of(pointGeneratorWithAmount(0L)));

        //then
        assertThat(pointService.depositOptimistic(memberId, amount)).isEqualTo(amount);
    }

    @Test
    @DisplayName("입금 로직 테스트")
    void deposit() {
        //given
        String memberId = "tester";
        Long amount = 100L;

        //when
        when(pointRepository.findByMemberIdForUpdate(memberId)).thenReturn(Optional.of(pointGeneratorWithAmount(0L)));

        //then
        assertThat(pointService.deposit(memberId, amount)).isEqualTo(amount);
    }

    @Test
    @DisplayName("출금 로직 에러 테스트")
    void withdrawError() {
        //given
        String memberId = "tester";
        Long amount = 100L;

        //when
        when(pointRepository.findByMemberIdForUpdate(memberId)).thenReturn(Optional.of(pointGeneratorWithAmount(0L)));

        //then
        assertThatThrownBy(() -> pointService.withdraw(memberId, amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("포인트가 부족합니다");
    }

    @Test
    @DisplayName("출금 로직 테스트")
    void withdraw() {
        //given
        String memberId = "tester";
        Long amount = 100L;

        //when
        when(pointRepository.findByMemberIdForUpdate(memberId)).thenReturn(Optional.of(pointGeneratorWithAmount(100L)));

        //then
        assertThat(pointService.withdraw(memberId, amount)).isEqualTo(0L);
    }

    @Test
    @DisplayName("포인트 정상 조회 테스트")
    void getPoint() {
        //given
        String memberId = "tester";

        //when
        when(pointRepository.findByMemberIdForUpdate(memberId)).thenReturn(Optional.of(pointGeneratorWithAmount(50L)));

        //then
        assertThat(pointService.getPoint(memberId)).isEqualTo(50L);
    }
}