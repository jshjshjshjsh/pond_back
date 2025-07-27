package com.itjamz.pond_back.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${gemini.api.secret}")
    private String geminiApiKey;

    public String getSummaryFromGemini(String prompt) {
        Client client = Client.builder().apiKey(geminiApiKey).build();

        prompt = "아래의 내용을 정리해서 나열해줘 \n" + prompt;

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        System.out.println(response.text());

        return response.text();
    }

}
