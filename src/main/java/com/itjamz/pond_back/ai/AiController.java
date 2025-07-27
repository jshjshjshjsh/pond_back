package com.itjamz.pond_back.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping("/summary")
        public ResponseEntity<String> aiSummary(@RequestBody String requireString){

        return ResponseEntity.ok(aiService.getSummaryFromGemini(requireString));
    }
}
