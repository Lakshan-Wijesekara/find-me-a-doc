package com.findmeadoc.application.dto;

public record DoctorRegistrationResponse(
        Long doctorId,
        String email,
        String name,
        String specialization,
        Double consultationFee
) {
}
