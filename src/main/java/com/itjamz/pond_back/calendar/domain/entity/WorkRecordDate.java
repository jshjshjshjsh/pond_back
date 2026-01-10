package com.itjamz.pond_back.calendar.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
public class WorkRecordDate {

    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;

    protected WorkRecordDate() {}

    @Builder
    public WorkRecordDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("종료일이 시작일보다 빠를 수 없습니다."); // 도메인 규칙!
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
