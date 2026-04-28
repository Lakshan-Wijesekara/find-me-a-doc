package com.findmeadoc.infrastructure.security.services;


public class SecuritySanitizer {

    /**
     * Masks PII and sanitizes the input string to prevent prompt injection.
     */
    public static String cleanPatientInput(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        String cleaned = input;

        // Length Truncation
        if (cleaned.length() > 500) {
            cleaned = cleaned.substring(0, 500);
        }

        // PII Masking: Emails
        // Matches standard email formats
        cleaned = cleaned.replaceAll("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", "[EMAIL SECURED]");

        // PII Masking: Phone Numbers
        // Matches formats like 123-456-7890, (123) 456-7890, 1234567890
        cleaned = cleaned.replaceAll("\\b(?:\\+\\d{1,3}[- ]?)?\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}\\b", "[PHONE SECURED]");

        // Prompt Injection Defense: Strip markdown formatting often used to confuse LLMs
        cleaned = cleaned.replaceAll("```", ""); // Remove code blocks
        cleaned = cleaned.replaceAll("(?i)(ignore previous instructions|system prompt|jailbreak)", "[FILTERED]");

        return cleaned.trim();
    }
}
