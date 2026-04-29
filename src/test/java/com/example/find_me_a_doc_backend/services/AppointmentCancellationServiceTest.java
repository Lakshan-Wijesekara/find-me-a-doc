package com.example.find_me_a_doc_backend.services;

import com.findmeadoc.application.dto.PatientNotificationResponse;
import com.findmeadoc.application.ports.EmailNotificationUseCase;
import com.findmeadoc.application.services.AppointmentCancellationService;
import com.findmeadoc.domain.exception.ResourceNotFoundException;
import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.Patient;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import com.findmeadoc.domain.repositories.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentCancellationServiceTest {

    // Test Data Variables
    private final Long appointmentId = 100L;
    private final String doctorEmail = "doctor@clinic.com";
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private EmailNotificationUseCase emailNotificationUseCase;
    @InjectMocks
    private AppointmentCancellationService cancellationService;
    // Mocks for deep entity relationships
    private Doctor mockDoctor;
    private Appointment mockAppointment;
    private Patient mockPatient;

    @BeforeEach
    void setUp() {
        // 1. Setup Doctor & Doctor User
        mockDoctor = mock(Doctor.class);
        User doctorUser = mock(User.class);
        lenient().when(mockDoctor.getId()).thenReturn(1L);
        lenient().when(mockDoctor.getUser()).thenReturn(doctorUser);
        lenient().when(doctorUser.getFullName()).thenReturn("Ruwan Gamage");

        // 2. Setup Patient & Patient User
        mockPatient = mock(Patient.class);
        User patientUser = mock(User.class);
        lenient().when(mockPatient.getId()).thenReturn(5L);
        lenient().when(mockPatient.getUser()).thenReturn(patientUser);
        lenient().when(patientUser.getFullName()).thenReturn("Saman Perera");
        lenient().when(patientUser.getEmail()).thenReturn("perera@email.com");

        // 3. Setup Appointment
        mockAppointment = mock(Appointment.class);
        lenient().when(mockAppointment.getId()).thenReturn(appointmentId);
        lenient().when(mockAppointment.getDoctor()).thenReturn(mockDoctor);
        lenient().when(mockAppointment.getPatient()).thenReturn(mockPatient);
        lenient().when(mockAppointment.getAppointmentDate()).thenReturn(LocalDate.of(2026, 5, 1));
    }

    @Test
    void successfullyCancelAppointmentAndSendNotifications() {
        // Arrange
        when(doctorRepository.findByUserEmail(doctorEmail)).thenReturn(Optional.of(mockDoctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Act
        cancellationService.execute(appointmentId, doctorEmail);

        // Assert
        // 1. Verify status was updated to Cancelled
        verify(mockAppointment, times(1)).setStatus("Cancelled");
        verify(appointmentRepository, times(1)).save(mockAppointment);

        // 2. Verify WebSocket message was sent
        verify(messagingTemplate, times(1)).convertAndSend(
                ArgumentMatchers.eq("/topic/patient/5"),
                ArgumentMatchers.any(PatientNotificationResponse.class)
        );

        // 3. Verify Email was sent
        verify(emailNotificationUseCase, times(1)).sendEmail(
                ArgumentMatchers.eq("perera@email.com"),
                ArgumentMatchers.eq("Appointment Cancellation Notice"),
                ArgumentMatchers.contains("Saman Perera")
        );
    }

    @Test
    void throwsExceptionWhenDoctorNotFound() {
        // Arrange
        when(doctorRepository.findByUserEmail(doctorEmail)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cancellationService.execute(appointmentId, doctorEmail);
        });

        assertEquals("Doctor not found", exception.getMessage());

        // Ensure no data was saved or sent
        verify(appointmentRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void throwsExceptionWhenAppointmentNotFound() {
        // Arrange
        when(doctorRepository.findByUserEmail(doctorEmail)).thenReturn(Optional.of(mockDoctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            cancellationService.execute(appointmentId, doctorEmail);
        });

        assertEquals("Appointment not found", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenDoctorDoesNotOwnAppointment() {
        // Arrange
        when(doctorRepository.findByUserEmail(doctorEmail)).thenReturn(Optional.of(mockDoctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(mockAppointment));

        // Create a different doctor (ID 99) who is trying to cancel Dr. Ruwan's appointment
        Doctor unauthorizedDoctor = mock(Doctor.class);
        when(unauthorizedDoctor.getId()).thenReturn(99L);
        when(mockAppointment.getDoctor()).thenReturn(unauthorizedDoctor);

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cancellationService.execute(appointmentId, doctorEmail);
        });

        assertEquals("You are not authorized to cancel this appointment.", exception.getMessage());

        // Ensure no messages were sent
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
        verify(emailNotificationUseCase, never()).sendEmail(anyString(), anyString(), anyString());
    }
}
