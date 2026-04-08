package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.dto.DoctorUpdateRequest;
import com.findmeadoc.application.ports.UpdateDoctorProfileUseCase;
import com.findmeadoc.application.ports.ViewDoctorProfileUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final ViewDoctorProfileUseCase viewDoctorProfileUseCase;
    private final UpdateDoctorProfileUseCase updateDoctorProfileUseCase;

    // Injecting the Use Case we just built
    public DoctorController(ViewDoctorProfileUseCase viewDoctorProfileUseCase, UpdateDoctorProfileUseCase updateDoctorProfileUseCase) {
        this.viewDoctorProfileUseCase = viewDoctorProfileUseCase;
        this.updateDoctorProfileUseCase = updateDoctorProfileUseCase;
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

    @PutMapping("/me")
    public ResponseEntity<DoctorProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DoctorUpdateRequest request) {

        // Get the email from the JWT badge
        String loggedInEmail = userDetails.getUsername();

        // Pass the email and the new data to update service
        DoctorProfileResponse response = updateDoctorProfileUseCase.updateDoctorProfile(loggedInEmail, request);

        // Return a 200 OK with the updated profile
        return ResponseEntity.ok(response);
    }

}