package com.findmeadoc.application.dto;


public record DoctorRegistrationRequest(
        String email,
        String password,
        String name,
        String specialization,
        Double consultationFee,
        String phoneNumber
) {

}
