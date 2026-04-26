package com.findmeadoc.application.dto;

public record TriageResponse(
        Boolean isFinalBriefReady,
        String nextQuestion,
        String urgencyLevel,
        String recommendedSpecialist,
        String viralLikelihood,
        String doctorBrief
) {
}