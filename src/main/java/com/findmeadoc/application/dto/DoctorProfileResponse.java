package com.findmeadoc.application.dto;

public record DoctorProfileResponse(
        Long id,
        String name,
        String specialization,
        Double consultationFee,
        String email,
        String phoneNumber
) {
}
