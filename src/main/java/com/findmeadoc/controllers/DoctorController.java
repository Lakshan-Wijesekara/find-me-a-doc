package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.ports.ViewDoctorProfileUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final ViewDoctorProfileUseCase viewDoctorProfileUseCase;

    // Injecting the Use Case we just built
    public DoctorController(ViewDoctorProfileUseCase viewDoctorProfileUseCase) {
        this.viewDoctorProfileUseCase = viewDoctorProfileUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // Spring extracts the email from the verified JWT badge
        String loggedInEmail = userDetails.getUsername();

        // We pass the email to use case to fetch the combined profile data
        DoctorProfileResponse response = viewDoctorProfileUseCase.viewDoctorProfile(loggedInEmail);

        // Return a 200 OK with the profile data to the React frontend
        return ResponseEntity.ok(response);
    }
}