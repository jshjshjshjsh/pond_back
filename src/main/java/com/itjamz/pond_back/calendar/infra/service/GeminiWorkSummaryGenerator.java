package com.itjamz.pond_back.calendar.infra.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.itjamz.pond_back.calendar.service.WorkSummaryGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeminiWorkSummaryGenerator implements WorkSummaryGenerator {

    @Value("${gemini.api.secret}")
    private String geminiApiKey;

    @Override
    public String generateSummary(String prompt) {
        Client client = Client.builder().apiKey(geminiApiKey).build();

        prompt = "아래의 내용을 분류하고 정리해서 나열해줘 \n" + prompt;

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        log.info(response.text());

        return response.text();
    }
}
