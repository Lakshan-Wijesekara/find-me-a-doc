package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.dto.DoctorUpdateRequest;
import com.findmeadoc.application.ports.UpdateDoctorProfileUseCase;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDoctorProfileService implements UpdateDoctorProfileUseCase {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public UpdateDoctorProfileService(
            DoctorRepository doctorRepository,
            UserRepository userRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional // Ensures the database updates safely!
    public DoctorProfileResponse updateDoctorProfile(String email, DoctorUpdateRequest request) {

        // Step 1: Fetch the existing doctor securely using the email
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found with email: " + email));

        // Step 2: Apply the updates to the Doctor entity
        doctor.setSpecialization(request.specialization());
        doctor.setConsultationFee(request.consultationFee());

        // Step 3: Apply the updates to the linked User entity
        User user = doctor.getUser();
        user.setPhoneNumber(request.phoneNumber());
        user.setFullName(request.name());

        // Step 4: Save the changes to the database
        doctorRepository.save(doctor);
        userRepository.save(user);

        // Step 5: Return the freshly updated profile data!
        return new DoctorProfileResponse(
                doctor.getId(),
                user.getFullName(),
                doctor.getSpecialization(),
                doctor.getConsultationFee(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

}
