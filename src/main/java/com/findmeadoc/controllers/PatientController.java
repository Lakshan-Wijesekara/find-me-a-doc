package com.findmeadoc.controllers;


import com.findmeadoc.application.dto.PatientProfileResponse;
import com.findmeadoc.application.services.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientProfileService patientProfileService;

    @GetMapping("/me")
    public ResponseEntity<PatientProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails.getUsername() holds the email from the JWT
        PatientProfileResponse profile = patientProfileService.viewPatientProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }
}