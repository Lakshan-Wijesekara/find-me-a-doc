package com.findmeadoc.application.dto;

import java.math.BigDecimal;

public record DoctorRegistrationResponse(
        Long doctorId,
        String email,
        String name,
        String specialization,
        BigDecimal consultationFee
) {
}
