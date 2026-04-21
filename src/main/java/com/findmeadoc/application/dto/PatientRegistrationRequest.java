package com.findmeadoc.application.dto;


public record PatientRegistrationRequest(
        String name,
        String email,
        String password,
        String phoneNumber
) {

}
