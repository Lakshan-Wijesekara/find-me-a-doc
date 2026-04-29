package com.example.find_me_a_doc_backend.services;

import com.findmeadoc.application.dto.DoctorRegistrationRequest;
import com.findmeadoc.application.dto.DoctorRegistrationResponse;
import com.findmeadoc.application.services.DoctorRegistrationService;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.Role;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorRegistrationService registrationService;

    // ArgumentCaptors to peek inside the objects passed to the repositories
    // Because need to test the new objects created
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Doctor> doctorCaptor;

    private DoctorRegistrationRequest mockRequest;
    private User mockSavedUser;
    private Doctor mockSavedDoctor;

    @BeforeEach
    void setUp() {
        // Prepare the incoming request
        mockRequest = new DoctorRegistrationRequest(
                "sarah@clinic.com",
                "SecurePass123",
                "Dr. Sarah Connor",
                "Neurology",
                new Double("200.00"),
                "555-0199"

        );

        // Prepare the mock User returned by the DB
        mockSavedUser = new User();
        mockSavedUser.setId(10L);
        mockSavedUser.setFullName(mockRequest.name());
        mockSavedUser.setEmail(mockRequest.email());
        mockSavedUser.setPhoneNumber(mockRequest.phoneNumber());
        mockSavedUser.setRole(Role.DOCTOR);
        mockSavedUser.setPassword("hashed_SecurePass123");

        // Prepare the mock Doctor returned by the DB
        mockSavedDoctor = new Doctor();
        mockSavedDoctor.setId(99L);
        mockSavedDoctor.setUser(mockSavedUser);
        mockSavedDoctor.setSpecialization(mockRequest.specialization());
        mockSavedDoctor.setConsultationFee(mockRequest.consultationFee());
    }

    @Test
    void successfullyRegistersDoctor() {
        // Arrange
        when(passwordEncoder.encode(mockRequest.password())).thenReturn("hashed_SecurePass123");
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(mockSavedDoctor);

        // Act
        DoctorRegistrationResponse response = registrationService.registerDoctor(mockRequest);

        // Assert - Verify the repositories were called exactly once
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(doctorRepository, times(1)).save(doctorCaptor.capture());

        // Verify the User was built correctly before saving
        User capturedUser = userCaptor.getValue();
        assertEquals("sarah@clinic.com", capturedUser.getEmail());
        assertEquals("hashed_SecurePass123", capturedUser.getPassword());
        assertEquals(Role.DOCTOR, capturedUser.getRole());

        // Verify the Doctor was built correctly before saving
        Doctor capturedDoctor = doctorCaptor.getValue();
        assertEquals("Neurology", capturedDoctor.getSpecialization());
        assertEquals(10L, capturedDoctor.getUser().getId()); // Ensures the saved user was linked

        // Verify the final Response
        assertNotNull(response);
        assertEquals(99L, response.doctorId());
        assertEquals("sarah@clinic.com", response.email());
        assertEquals("Dr. Sarah Connor", response.name());
        assertEquals("Neurology", response.specialization());
        assertEquals(new Double("200.00"), response.consultationFee());
    }

    @Test
    void throwsExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(passwordEncoder.encode(mockRequest.password())).thenReturn("hashed_SecurePass123");

        // Simulate a database crash because the email is already taken
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Email already exists"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            registrationService.registerDoctor(mockRequest);
        });

        // Verify that because the User save failed, the Doctor save was NEVER called
        verify(doctorRepository, never()).save(any(Doctor.class));
    }
}
