package com.itjamz.pond_back.calendar.service;

import com.itjamz.pond_back.calendar.repository.WorkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final WorkHistoryRepository workHistoryRepository;
}
