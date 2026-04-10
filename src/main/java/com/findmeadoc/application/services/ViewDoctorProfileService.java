package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.ports.ViewDoctorProfileUseCase;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ViewDoctorProfileService implements ViewDoctorProfileUseCase {

    private final DoctorRepository doctorRepository;

    public ViewDoctorProfileService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public DoctorProfileResponse viewDoctorProfile(String email) {

        // Step 1: Fetch the Doctor entity using the email
        Doctor newDoctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));

        // Step 2: Return the mapped DoctorProfileResponse
        return new DoctorProfileResponse(
                newDoctor.getId(),
                newDoctor.getUser().getFullName(),
                newDoctor.getSpecialization(),
                newDoctor.getConsultationFee(),
                newDoctor.getUser().getEmail(),
                newDoctor.getUser().getPhoneNumber()

        );
    }
}