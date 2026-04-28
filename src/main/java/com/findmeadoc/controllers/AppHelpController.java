package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.AppHelpRequest;
import com.findmeadoc.application.dto.AppHelpResponse;
import com.findmeadoc.application.ports.ProvideAppHelpUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping; // Added this
import org.springframework.web.bind.annotation.RestController; // Added this

@RestController
@RequestMapping("/api/v1/help")
public class AppHelpController {

    private final ProvideAppHelpUseCase appHelpUseCase;

    // Standard constructor injection
    public AppHelpController(ProvideAppHelpUseCase appHelpUseCase) {
        this.appHelpUseCase = appHelpUseCase;
    }

    @PostMapping("/chat")
    public ResponseEntity<AppHelpResponse> chat(@RequestBody AppHelpRequest request) {
        return ResponseEntity.ok(appHelpUseCase.getHelpResponse(request));
    }
}