package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.PatientNotificationResponse;
import com.findmeadoc.application.ports.CancelAppointmentUseCase;
import com.findmeadoc.application.ports.EmailNotificationUseCase;
import com.findmeadoc.domain.exception.ResourceNotFoundException;
import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.Doctor;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import com.findmeadoc.domain.repositories.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AppointmentCancellationService implements CancelAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailNotificationUseCase emailNotificationUseCase;

    @Override
    @Transactional
    public void execute(Long appointmentId, String doctorEmail) {
        // verify the doctor making the request
        Doctor doctor = doctorRepository.findByUserEmail(doctorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // Find the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Check if the doctor actually owns this appointment
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new IllegalStateException("You are not authorized to cancel this appointment.");
        }

        // Update the status
        appointment.setStatus("Cancelled");
        appointmentRepository.save(appointment);

        // Build and send the real-time notification to the PATIENT
        PatientNotificationResponse notification = new PatientNotificationResponse(
                "Dr. " + doctor.getUser().getFullName() + " has cancelled your appointment on " + appointment.getAppointmentDate(),
                appointment.getId(),
                "Cancelled"
        );

        String patientChannel = "/topic/patient/" + appointment.getPatient().getId();
        messagingTemplate.convertAndSend(patientChannel, notification);

        String patientEmail = appointment.getPatient().getUser().getEmail();
        String emailSubject = "Appointment Cancellation Notice";
        String emailBody = String.format(
                "Dear %s,\n\nThis is a notification that your upcoming appointment with Dr. %s on %s has been cancelled.\n\nPlease log into your dashboard to book a new time.\n\nRegards,\nFindMeADoc Administration",
                appointment.getPatient().getUser().getFullName(),
                doctor.getUser().getFullName(),
                appointment.getAppointmentDate()
        );

        emailNotificationUseCase.sendEmail(patientEmail, emailSubject, emailBody);
    }
}
