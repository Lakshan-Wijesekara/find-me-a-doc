package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.TriageResponse;
import com.findmeadoc.application.ports.AITriagePort;
import com.findmeadoc.infrastructure.security.services.SecuritySanitizer;
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
        String rawSymptoms = triageRequest.symptoms();

        // Sanitize the data and mask PII
        String safeSymptoms = SecuritySanitizer.cleanPatientInput(rawSymptoms);

        TriageResponse response = aiTriagePort.analyzeSymptoms(chatId, safeSymptoms);

        return ResponseEntity.ok(response);
    }
}
