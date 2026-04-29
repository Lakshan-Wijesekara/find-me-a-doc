package com.example.find_me_a_doc_backend.services;

import com.findmeadoc.application.dto.PatientRegistrationRequest;
import com.findmeadoc.application.dto.PatientRegistrationResponse;
import com.findmeadoc.application.services.PatientRegistrationService;
import com.findmeadoc.domain.models.Patient;
import com.findmeadoc.domain.models.Role;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.PatientRepository;
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
class PatientRegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientRegistrationService registrationService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Patient> patientCaptor;

    private PatientRegistrationRequest mockRequest;
    private User mockSavedUser;
    private Patient mockSavedPatient;

    @BeforeEach
    void setUp() {
        // Prepare the incoming request for a 25-year-old patient
        mockRequest = new PatientRegistrationRequest(
                "John Doe",
                "john@email.com",
                "Secret123",
                "077-1234567",
                25
        );

        // Mock the User object saved in the first step
        mockSavedUser = new User();
        mockSavedUser.setId(50L);
        mockSavedUser.setFullName(mockRequest.name());
        mockSavedUser.setEmail(mockRequest.email());
        mockSavedUser.setRole(Role.PATIENT);
        mockSavedUser.setPassword("encoded_Secret123");

        // Mock the Patient object saved in the second step
        mockSavedPatient = new Patient();
        mockSavedPatient.setId(1L);
        mockSavedPatient.setUser(mockSavedUser);
        mockSavedPatient.setAge(mockRequest.age());
    }

    @Test
    void successfullyRegistersPatient() {
        // Arrange
        when(passwordEncoder.encode(mockRequest.password())).thenReturn("encoded_Secret123");
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(mockSavedPatient);

        // Act
        PatientRegistrationResponse response = registrationService.registerPatient(mockRequest);

        // Assert - Use Captors to verify the "Black Box" object creation
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(patientRepository, times(1)).save(patientCaptor.capture());

        // 1. Verify User State
        User capturedUser = userCaptor.getValue();
        assertEquals(Role.PATIENT, capturedUser.getRole());
        assertEquals("encoded_Secret123", capturedUser.getPassword());

        // 2. Verify Patient State
        Patient capturedPatient = patientCaptor.getValue();
        assertEquals(25, capturedPatient.getAge());
        assertEquals(50L, capturedPatient.getUser().getId());

        // 3. Verify Response
        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("john@email.com", response.email());
        assertEquals(25, response.age());
    }

    @Test
    void shouldAbortPatientSaveIfUserSaveFails() {
        // Arrange
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        // Simulate a duplicate email error in the User table
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate Email"));

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            registrationService.registerPatient(mockRequest);
        });

        // Verify the code never reached the patientRepository.save() call
        verify(patientRepository, never()).save(any(Patient.class));
    }
}
