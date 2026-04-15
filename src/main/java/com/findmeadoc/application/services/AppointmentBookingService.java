package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.CreateAppointmentRequest;
import com.findmeadoc.application.dto.CreateAppointmentResponse;
import com.findmeadoc.application.ports.BookAppointmentUseCase;
import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.User;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import com.findmeadoc.domain.repositories.DoctorRepository;
import com.findmeadoc.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.findmeadoc.domain.exception.ResourceNotFoundException;
import com.findmeadoc.domain.models.Doctor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppointmentBookingService implements BookAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional
    public CreateAppointmentResponse execute(CreateAppointmentRequest request, String patientEmail) {
        // Get the doctor details
        Doctor doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));

        // Get the patient details
        User patient = userRepository.findByEmail(patientEmail)
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

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        String doctorName = doctor.getUser().getFullName();

        return new CreateAppointmentResponse(
                doctorName,
                savedAppointment.getId().toString(),
                savedAppointment.getAppointmentDate(),
                savedAppointment.getAppointmentTime()
        );
    }
}
