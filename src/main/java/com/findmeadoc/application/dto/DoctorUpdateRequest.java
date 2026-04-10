package com.findmeadoc.application.dto;

public record DoctorUpdateRequest(
        String specialization,
        Double consultationFee,
        String phoneNumber
) {
}
