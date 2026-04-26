package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.TriageResponse;
import com.findmeadoc.application.ports.AITriagePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/triage")
@CrossOrigin(origins = "http://localhost:5173")
public class AiTriageController {
    private final AITriagePort aiTriagePort;

    public AiTriageController(AITriagePort aiTriagePort) {
        this.aiTriagePort = aiTriagePort;
    }

    @PostMapping("/diagnose")
    public ResponseEntity<TriageResponse> analyzeSymptoms(
            @RequestBody TriageRequest triageRequest) {
        // Get chatId
        String chatId = triageRequest.chatId();
        //Get symptoms
        String symptoms = triageRequest.symptoms();

        TriageResponse response = aiTriagePort.analyzeSymptoms(chatId, symptoms);

        return ResponseEntity.ok(response);
    }
}
