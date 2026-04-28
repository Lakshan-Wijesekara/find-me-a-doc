package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.AppHelpRequest;
import com.findmeadoc.application.dto.AppHelpResponse;
import com.findmeadoc.application.ports.AiModelPort;
import com.findmeadoc.application.ports.ProvideAppHelpUseCase;
import com.findmeadoc.infrastructure.security.services.SecuritySanitizer;
import org.springframework.stereotype.Service;


@Service
public class AppHelpService implements ProvideAppHelpUseCase {
    private final AiModelPort aiModelPort;

    public AppHelpService(AiModelPort aiModelPort) {
        this.aiModelPort = aiModelPort;
    }

    @Override
    public AppHelpResponse getHelpResponse(AppHelpRequest request) {
        // Delegate straight to the Spring AI adapter
        String rawMessage = request.message();
        String sanitizedMessage = SecuritySanitizer.cleanPatientInput(rawMessage);
        String aiReply = aiModelPort.generateResponse(request.chatId(), sanitizedMessage);
        return new AppHelpResponse(aiReply);
    }
}
