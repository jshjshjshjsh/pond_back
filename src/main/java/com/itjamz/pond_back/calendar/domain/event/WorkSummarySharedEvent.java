package com.itjamz.pond_back.calendar.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WorkSummarySharedEvent {
    private Long workSummaryId;
    private String writerName;
    private int year;
    private int month;
}
