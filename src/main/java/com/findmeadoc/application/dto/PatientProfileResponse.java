package com.findmeadoc.application.dto;

public record PatientProfileResponse(
        Long id,
        String name,
        String email
) {
}
