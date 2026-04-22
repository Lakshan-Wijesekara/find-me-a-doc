package com.findmeadoc.application.dto;

public record PatientRegistrationResponse(
        Long userId,
        String email,
        String name,
        Integer age
) {
}
