package com.findmeadoc.application.ports;

public interface AiModelPort {
    /**
     * Sends the prompt/history to the AI model and returns the text response.
     */
    String generateResponse(String chatId, String userMessage);
}
