package com.findmeadoc.application.dto;

public record DoctorUpdateRequest(
        String name,
        String specialization,
        Double consultationFee,
        String phoneNumber
) {
}
