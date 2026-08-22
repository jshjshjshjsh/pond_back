package com.grove.pond_back.ai;

import com.grove.pond_back.calendar.service.WorkSummaryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WorkSummaryGenerator workSummaryGenerator;

    public String getSummaryFromGemini(String prompt) {

        return workSummaryGenerator.generateSummary(prompt);
    }

}
