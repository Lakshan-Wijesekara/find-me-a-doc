package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.PatientRegistrationRequest;
import com.findmeadoc.application.dto.PatientRegistrationResponse;
import com.findmeadoc.application.ports.PatientRegistrationUseCase;
import com.findmeadoc.domain.models.Patient;
import com.findmeadoc.domain.models.Role;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.PatientRepository;
import com.findmeadoc.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientRegistrationService implements PatientRegistrationUseCase {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientRegistrationService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Used to avoid half created profiles.
    public PatientRegistrationResponse registerPatient(PatientRegistrationRequest request) {

        // Step 1: Create the base User entity
        User newUser = new User();
        newUser.setEmail(request.email());
        newUser.setFullName(request.name());
        newUser.setPhoneNumber(request.phoneNumber());
        newUser.setRole(Role.PATIENT);

        // TODO: Hash the password and set it on newUser
        newUser.setPassword(passwordEncoder.encode(request.password()));

        // Save the User to the database
        User savedUser = userRepository.save(newUser);

        // Create the Patient entity and link the saved User
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patient.setAge(request.age());

        // Save the Patient to the database
        Patient savedPatient = patientRepository.save(patient);

        // Return the PatientRegistrationResponse
        return new PatientRegistrationResponse(
                savedPatient.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedPatient.getAge()
        );
    }
}
