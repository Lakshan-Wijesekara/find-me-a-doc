package com.findmeadoc.infrastructure.ai;

import com.findmeadoc.application.ports.AiModelPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class OllamaAppHelpAdapter implements AiModelPort {
    private final ChatClient chatClient;

    public OllamaAppHelpAdapter(ChatClient.Builder builder) {
        // Reuse your exact memory pattern
        InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(memoryRepository)
                .maxMessages(10) // Remember last 10 messages
                .build();

        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Override
    public String generateResponse(String chatId, String userMessage) {
        String systemRules = """
                You are the 'Find-Me-A-Doc' App Navigation Assistant. Your ONLY job is to help users use this application.
                
                App Features you can explain:
                1. Booking an appointment: Go to Dashboard -> Find a Doctor -> Select Date/Time -> Confirm.
                2. Canceling an appointment: Go to Dashboard -> Click Cancel -> Confirm.
                3. Using the AI Triage: Click the 'Symptom Analyzer', a blue floating button -> Chat -> Get a recommended specialist.
                
                CRITICAL RULES:
                - NEVER give medical advice, diagnoses, or triage. If the user mentions symptoms or asks a medical question, you MUST reply: "I am only an app assistant. Please use the Symptom Analyzer button on your dashboard for medical triage."
                - Keep your answers short, friendly, and strictly related to navigating the app.
                - DO NOT use markdown formatting. Use plain text.
                """;

        return chatClient.prompt()
                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
                .system(systemRules)
                .user(userMessage)
                .call()
                .content(); // Returns plain text instead of a JSON entity
    }
}
