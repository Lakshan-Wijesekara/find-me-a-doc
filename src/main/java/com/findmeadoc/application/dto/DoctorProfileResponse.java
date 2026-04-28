package com.findmeadoc.application.dto;

import java.io.Serializable;

public record DoctorProfileResponse(
        Long id,
        String name,
        String specialization,
        Double consultationFee,
        String email,
        String phoneNumber
) implements Serializable {
}
