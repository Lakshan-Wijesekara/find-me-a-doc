package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.*;
import com.findmeadoc.application.ports.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {
    private final BookAppointmentUseCase bookAppointmentUseCase;
    private final GetPatientAppointmentUseCase getPatientAppointmentUseCase;
    private final GetAvailableSlotsUseCase getAvailableSlotsUseCase;
    private final GetDoctorAppointmentsUseCase getDoctorAppointmentsUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    public AppointmentController(
            BookAppointmentUseCase bookAppointmentUseCase,
            GetPatientAppointmentUseCase getPatientAppointmentUseCase,
            GetAvailableSlotsUseCase getAvailableSlotsUseCase,
            GetDoctorAppointmentsUseCase getDoctorAppointmentsUseCase,
            CancelAppointmentUseCase cancelAppointmentUseCase
    ) {
        this.bookAppointmentUseCase = bookAppointmentUseCase;
        this.getPatientAppointmentUseCase = getPatientAppointmentUseCase;
        this.getAvailableSlotsUseCase = getAvailableSlotsUseCase;
        this.getDoctorAppointmentsUseCase = getDoctorAppointmentsUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
    }

    @PostMapping
    public ResponseEntity<CreateAppointmentResponse> bookAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateAppointmentRequest request) {

        // Get the patient email from the JWT badge
        String patientEmail = userDetails.getUsername(); // Still gets the useremail the unique identifier

        // Pass the email and the new data to update service
        CreateAppointmentResponse response = bookAppointmentUseCase.execute(request, patientEmail);

        // Return a 200 OK with the updated profile
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDashboardResponse>> getPatientAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {

        String patientEmail = userDetails.getUsername();

        List<AppointmentDashboardResponse> appointments = getPatientAppointmentUseCase.execute(patientEmail);

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Pass the request to the service layer
        List<AvailableSlotResponse> availableSlots = getAvailableSlotsUseCase.execute(doctorId, date);

        // Return 200 OK with the list of available times
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping("/doctor")
    public ResponseEntity<List<DoctorAppointmentDashboardResponse>> getDoctorAppointments(
            @AuthenticationPrincipal UserDetails userDetails) {

        // Extract the doctor's email from the JWT
        String doctorEmail = userDetails.getUsername();

        // Fetch the appointments via the Service
        List<DoctorAppointmentDashboardResponse> appointments = getDoctorAppointmentsUseCase.execute(doctorEmail);

        // Return a 200 OK with the list
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // The Principal's name is typically the doctor email
        String doctorEmail = userDetails.getUsername();

        // Execute the business logic
        cancelAppointmentUseCase.execute(appointmentId, doctorEmail);

        // Return a friendly JSON response
        return ResponseEntity.ok(Map.of("message", "Appointment successfully cancelled."));
    }

}
