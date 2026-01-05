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
@NoArgsConstructor
public class WorkRecordDate {

    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;

    @Builder
    public WorkRecordDate(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: valid 추가
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateWorkRecordDate(LocalDateTime startDate, LocalDateTime endDate){
        if (startDate != null)
            this.startDate = startDate;
        if (endDate != null)
            this.endDate = endDate;
    }
}
