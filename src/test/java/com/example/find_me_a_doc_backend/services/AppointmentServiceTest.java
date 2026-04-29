package com.example.find_me_a_doc_backend.services;

import com.findmeadoc.application.dto.CreateAppointmentRequest;
import com.findmeadoc.application.services.AppointmentBookingService;
import com.findmeadoc.domain.exception.ResourceNotFoundException;
import com.findmeadoc.domain.models.*;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class) // Enabling mockito
class AppointmentServiceTest {

    private Patient mockPatient;
    private Doctor mockDoctor;
    private Appointment mockAppointment;
    private CreateAppointmentRequest mockRequest;
    private String patientEmail;

    @Mock // Mocking the repos
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @InjectMocks // Real instance of Appointment booking service
    private AppointmentBookingService appointmentBookingService;

    @BeforeEach
    void setUp() {
        patientEmail = "patienttest@example.com";

        mockRequest = new CreateAppointmentRequest(
                101L,
                java.time.LocalDate.now().plusDays(1), // Tomorrow's date
                java.time.LocalTime.of(10, 0),// 10:00 AM
                Map.of(
                        "doctorBrief", "Severe chest pain, possible cardiac issue",
                        "preSelectedSpecialty", "Cardiology"
                )
        );

        // 1. Build the User and Patient 🧑‍⚕️
        User patientUser = new User();
        patientUser.setId(10L);
        patientUser.setEmail(patientEmail);
        patientUser.setFullName("Jane Doe");
        patientUser.setRole(Role.PATIENT);

        mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setUser(patientUser);
        mockPatient.setAge(30);

        // 2. Build the User and Doctor
        User doctorUser = new User();
        doctorUser.setId(20L);
        doctorUser.setFullName("Dr. Ruwan Gamage");
        doctorUser.setRole(Role.DOCTOR);

        mockDoctor = new Doctor();
        mockDoctor.setId(101L);
        mockDoctor.setUser(doctorUser);
        mockDoctor.setSpecialization("Diagnostician");
        mockDoctor.setConsultationFee(150.0);
        mockDoctor.setIsVerified(true);

        // 3. Build the expected Appointment
        mockAppointment = new Appointment();
        mockAppointment.setPatient(mockPatient);
        mockAppointment.setId(123L);
        mockAppointment.setDoctor(mockDoctor);
        mockAppointment.setAppointmentDate(mockRequest.appointmentDate());
        mockAppointment.setAppointmentTime(mockRequest.appointmentTime());
        mockAppointment.setStatus("SCHEDULED");
        mockAppointment.setPaymentStatus("Pending");
        mockAppointment.setCreatedAt(java.time.LocalDate.now());
    }

    @Test
    void successfullyBookAppointment() {
        // Arrange
        // Find patient
        when(patientRepository.findByUserEmail(patientEmail))
                .thenReturn(java.util.Optional.of(mockPatient));

        // Find doctor
        when(doctorRepository.findByIdWithLock(mockRequest.doctorId()))
                .thenReturn(java.util.Optional.of(mockDoctor));

        // Mock the availability checking
        when(appointmentRepository.findBookedSlotCountByDoctorAndDate(
                mockRequest.doctorId(), mockRequest.appointmentDate()))
                .thenReturn(new java.util.HashMap<>());

        // Mock saving the appointment to db
        when(appointmentRepository.save(org.mockito.ArgumentMatchers.any(Appointment.class)))
                .thenReturn(mockAppointment);

        // ACT
        // Call the actual method
        com.findmeadoc.application.dto.CreateAppointmentResponse response =
                appointmentBookingService.execute(mockRequest, patientEmail);

        // ASSERT
        // Verify the repo save method was called at least once
        org.mockito.Mockito.verify(appointmentRepository, org.mockito.Mockito.times(1))
                .save(org.mockito.ArgumentMatchers.any(Appointment.class));

        org.mockito.Mockito.verify(messagingTemplate, org.mockito.Mockito.times(1))
                .convertAndSend(
                        org.mockito.ArgumentMatchers.eq("/topic/doctor/" + mockRequest.doctorId()),
                        Optional.ofNullable(ArgumentMatchers.any())
                );

        org.junit.jupiter.api.Assertions.assertNotNull(response);

        //Assert the responses
        org.junit.jupiter.api.Assertions.assertEquals("Dr. Ruwan Gamage", response.doctorName());
        org.junit.jupiter.api.Assertions.assertEquals(mockRequest.appointmentDate(), response.appointmentDate());
        org.junit.jupiter.api.Assertions.assertEquals(mockRequest.appointmentTime(), response.appointmentTime());
    }

    @Test
    void throwExceptionWhenSlotsAreFullyBooked() {
        // Arrange
        when(doctorRepository.findByIdWithLock(mockRequest.doctorId()))
                .thenReturn(java.util.Optional.of(mockDoctor));

        // Mock the availability checking to return 10 bookings for that slot
        java.util.Map<java.time.LocalTime, Integer> bookedSlots = new java.util.HashMap<>();
        bookedSlots.put(mockRequest.appointmentTime(), 10); // Fully booked

        when(appointmentRepository.findBookedSlotCountByDoctorAndDate(
                mockRequest.doctorId(), mockRequest.appointmentDate()))
                .thenReturn(bookedSlots);

        // ACT & ASSERT
        IllegalStateException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> appointmentBookingService.execute(mockRequest, patientEmail) //Lambda wrapper - runs inside the service and catches by assertThrows
        );

        org.junit.jupiter.api.Assertions.assertEquals("This time slot is fully booked.", exception.getMessage());

        // Assert that the patient was not fetched
        org.mockito.Mockito.verify(patientRepository, org.mockito.Mockito.never())
                .findByUserEmail(org.mockito.ArgumentMatchers.anyString());

        // Prove the double-booking was prevented
        org.mockito.Mockito.verify(appointmentRepository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Appointment.class));
    }

    @Test
    void throwExceptionWhenDoctorDoesNotExist() {
        when(doctorRepository.findByIdWithLock(mockRequest.doctorId()))
                .thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentBookingService.execute(mockRequest, patientEmail)
        );

        org.junit.jupiter.api.Assertions.assertEquals("Doctor not found with id: " + 101L, exception.getMessage());

        org.mockito.Mockito.verify(appointmentRepository, org.mockito.Mockito.never())
                .findBookedSlotCountByDoctorAndDate(org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.any());

        org.mockito.Mockito.verify(appointmentRepository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Appointment.class));
    }

    @Test
    void throwExceptionWhenPatientDoesNotExist() {
        when(doctorRepository.findByIdWithLock(mockRequest.doctorId()))
                .thenReturn(java.util.Optional.of(mockDoctor));

        when(patientRepository.findByUserEmail(patientEmail))
                .thenReturn(java.util.Optional.empty());

        when(appointmentRepository.findBookedSlotCountByDoctorAndDate(
                mockRequest.doctorId(), mockRequest.appointmentDate()))
                .thenReturn(new java.util.HashMap<>());

        ResourceNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentBookingService.execute(mockRequest, patientEmail)
        );

        org.junit.jupiter.api.Assertions.assertEquals("Patient not found with email: " + "patienttest@example.com", exception.getMessage());

        org.mockito.Mockito.verify(appointmentRepository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Appointment.class));

    }

}