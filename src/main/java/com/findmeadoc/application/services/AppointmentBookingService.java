package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.AppointmentNotificationResponse;
import com.findmeadoc.application.dto.CreateAppointmentRequest;
import com.findmeadoc.application.dto.CreateAppointmentResponse;
import com.findmeadoc.application.ports.BookAppointmentUseCase;
import com.findmeadoc.domain.exception.ResourceNotFoundException;
import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.models.Patient;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentBookingService implements BookAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public CreateAppointmentResponse execute(CreateAppointmentRequest request, String patientEmail) {
        // Get the doctor details
        Doctor doctor = doctorRepository.findByIdWithLock(request.doctorId()) // Updated with pessimistic booking
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));

        // Fetch the map of current bookings for that day
        Map<LocalTime, Integer> dailyBookings = appointmentRepository
                .findBookedSlotCountByDoctorAndDate(request.doctorId(), request.appointmentDate());

        // Get the specific count for the requested time (default to 0 if empty)
        int currentBookings = dailyBookings.getOrDefault(request.appointmentTime(), 0);

        // Check if it hit the limit
        if (currentBookings >= 10) {
            throw new IllegalStateException("This time slot is fully booked.");
        }

        // Get the patient details
        Patient patient = patientRepository.findByUserEmail(patientEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + patientEmail));

        // Create the appointment entity
        Appointment newAppointment = new Appointment();
        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);
        newAppointment.setAppointmentDate(request.appointmentDate());
        newAppointment.setAppointmentTime(request.appointmentTime());
        newAppointment.setStatus("Scheduled");
        newAppointment.setPaymentStatus("Pending");
        newAppointment.setCreatedAt(LocalDate.now());
        if (request.aiBrief() != null && !request.aiBrief().isEmpty()) {
            newAppointment.setAiBrief(request.aiBrief());
        }

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        AppointmentNotificationResponse notification = new AppointmentNotificationResponse(
                savedAppointment.getId().toString(),
                patient.getUser().getFullName(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getAppointmentTime(),
                "You have a new appointment!"
        );

        messagingTemplate.convertAndSend("/topic/doctor/" + request.doctorId(), notification);

        String doctorName = doctor.getUser().getFullName();

        return new CreateAppointmentResponse(
                doctorName,
                savedAppointment.getId().toString(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getAppointmentTime()
        );
    }
}
