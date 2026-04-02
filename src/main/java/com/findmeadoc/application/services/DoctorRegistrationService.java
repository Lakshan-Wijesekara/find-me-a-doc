package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorRegistrationRequest;
import com.findmeadoc.application.dto.DoctorRegistrationResponse;
import com.findmeadoc.application.ports.DoctorRegistrationUseCase;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.Role;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorRegistrationService implements DoctorRegistrationUseCase {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorRegistrationService(
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Used to avoid half created profiles.
    public DoctorRegistrationResponse registerDoctor(DoctorRegistrationRequest request) {

        // Step 1: Create the base User entity
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setFullName(request.name());
        newUser.setPhoneNumber(request.phoneNumber());
        newUser.setRole(Role.DOCTOR);

        // TODO: Hash the password and set it on newUser
        newUser.setPassword(passwordEncoder.encode(request.password()));

        // Save the User to the database
        User savedUser = userRepository.save(newUser);

        // Create the Doctor entity and link the saved User
        Doctor doctor = new Doctor();
        doctor.setUser(savedUser);
        doctor.setSpecialization(request.specialization());
        doctor.setConsultationFee(request.consultationFee());

        // Save the Doctor to the database
        Doctor savedDoctor = doctorRepository.save(doctor);

        // Return the DoctorRegistrationResponse
        return new DoctorRegistrationResponse(
                savedDoctor.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedDoctor.getSpecialization(),
                savedDoctor.getConsultationFee()
        );
    }
}
