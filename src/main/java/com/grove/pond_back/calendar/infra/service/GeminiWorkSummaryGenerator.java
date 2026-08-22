package com.grove.pond_back.calendar.infra.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.grove.pond_back.calendar.service.WorkSummaryGenerator;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GeminiWorkSummaryGenerator implements WorkSummaryGenerator {

    @Value("${gemini.api.secret}")
    private String geminiApiKey;

    @Override
    @CircuitBreaker(name = "geminiApi", fallbackMethod = "fallbackGenerateSummary")
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

    // 외부 인프라 통제 불능 시 실행될 Fallback 메서드 추가
    public String fallbackGenerateSummary(String prompt, Throwable t) {
        log.error("[장애 격리] Gemini API 응답 지연/실패. 원인: {}", t.getMessage());
        return "AI 서버 지연 중입니다. 요약 기능은 잠시 후 다시 시도해주세요.";
    }
}
