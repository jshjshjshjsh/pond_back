package com.itjamz.pond_back.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.itjamz.pond_back.calendar.service.WorkSummaryGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final WorkSummaryGenerator workSummaryGenerator;

    public String getSummaryFromGemini(String prompt) {

        return workSummaryGenerator.generateSummary(prompt);
    }

}
