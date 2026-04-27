package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.PatientProfileResponse;
import com.findmeadoc.application.ports.ViewPatientProfileUseCase;
import com.findmeadoc.domain.models.Patient;
import com.findmeadoc.domain.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientProfileService implements ViewPatientProfileUseCase {
    private final PatientRepository patientRepository;

    @Override
    public PatientProfileResponse viewPatientProfile(String email) {
        Patient newPatient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));

        return new PatientProfileResponse(
                newPatient.getId(),
                newPatient.getUser().getFullName(),
                newPatient.getUser().getEmail()
        );
    }
}
