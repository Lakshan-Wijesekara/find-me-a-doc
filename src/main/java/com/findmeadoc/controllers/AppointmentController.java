package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.*;
import com.findmeadoc.application.ports.BookAppointmentUseCase;
import com.findmeadoc.application.ports.GetPatientAppointmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {
    private final BookAppointmentUseCase bookAppointmentUseCase;
    private final GetPatientAppointmentUseCase getPatientAppointmentUseCase;

    public  AppointmentController(BookAppointmentUseCase bookAppointmentUseCase, GetPatientAppointmentUseCase getPatientAppointmentUseCase) {
        this.bookAppointmentUseCase = bookAppointmentUseCase;
        this.getPatientAppointmentUseCase = getPatientAppointmentUseCase;
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

}
