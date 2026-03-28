package com.findmeadoc.application.dto;

import java.math.BigDecimal;

public record DoctorRegistrationRequest(
        String email,
        String password,
        String name,
        String specialization,
        BigDecimal consultationFee,
        String phoneNumber
) {

}
