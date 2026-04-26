package com.findmeadoc.infrastructure.ai;

import com.findmeadoc.application.dto.TriageResponse;
import com.findmeadoc.application.ports.AITriagePort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class OllamaAITriageAdapter implements AITriagePort {
    private final ChatClient chatClient;

    public OllamaAITriageAdapter(ChatClient.Builder builder) {
        // Create the repository that holds the data in memory
        InMemoryChatMemoryRepository memoryRepository = new InMemoryChatMemoryRepository();

        // Wrap it in a WindowMemory so the AI only remembers the last 10 messages (saves tokens!)
        ChatMemory chatMemory = MessageWindowChatMemory.builder() // Here it had default memory for 20 and we limited it to 10
                .chatMemoryRepository(memoryRepository)
                .maxMessages(10)
                .build();

        // Build the client with the advisor
        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) // Here the advisor needs the memory right away
                .build();
    }

    @Override
    public TriageResponse analyzeSymptoms(String chatId, String patientSymptoms) {

        // To avoid LLM trying to send markdowns instead of JSONs and break the responses, define a law for the LLM
        // Did some prompt engineering to first, null issue, second, not asking questions, third, fix asking too many questions, fourth, when asked to ask questions but limited it stops quickly, refined the prompt to ask good 2 questions and provide a proper response to the user
        // Finally still the LLM refuse to follow the text system rules so had to use a json like structure to tell it what I expect.
        String systemRules = """
                You are an expert medical triage assistant. Your goal is safe, accurate, and efficient triage.
                Aim to conclude in 1 to 3 questions maximum.
                
                You MUST output ONLY valid JSON matching one of these two exact states:
                
                STATE 1: NEED MORE INFO (Use this if symptoms are vague)
                {
                  "isFinalBriefReady": false,
                  "nextQuestion": "<Ask TWO specific clarifying question here>",
                  "urgencyLevel": "",
                  "recommendedSpecialist": "",
                  "viralLikelihood": "",
                  "doctorBrief": ""
                }
                
                STATE 2: READY TO CONCLUDE (Use this when you have enough info, or after 3 questions)
                {
                  "isFinalBriefReady": true,
                  "nextQuestion": "",
                  "urgencyLevel": "<Low, Medium, or High>",
                  "recommendedSpecialist": "<MUST BE THE MEDICAL FIELD. STRICTLY USE ONE OF: Dermatology, Cardiology, Pediatrics, General Medicine, Orthopedics, Neurology, or ER>",
                  "viralLikelihood": "<Unlikely, Possible, High, or Can't Decide>",
                  "doctorBrief": "<Write a 1-2 sentence medical summary here>"
                }
                
                CRITICAL RULES:
                - NEVER use practitioner titles like 'Cardiologist' or 'Dermatologist'. You MUST use the field name like 'Cardiology' or 'Dermatology'.
                - NEVER mix the states.
                - DO NOT use markdown formatting (no ```json).
                - DO NOT add conversational text.
                - THE DOCTOR BRIEF: When concluding, you ABSOLUTELY MUST write a MAXIMUM OF 20 WORDS in 'doctorBrief'. Keep it extremely concise.
                """;

        return chatClient.prompt()
                .advisors(a -> a.param("chat_memory_conversation_id", chatId))
                .system(systemRules) // Inject the rules
                .user(patientSymptoms) // user input
                .call()
                .entity(TriageResponse.class);
    }
}
