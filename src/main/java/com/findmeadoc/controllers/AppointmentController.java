package com.findmeadoc.controllers;

import com.findmeadoc.application.dto.CreateAppointmentRequest;
import com.findmeadoc.application.dto.CreateAppointmentResponse;
import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.dto.DoctorUpdateRequest;
import com.findmeadoc.application.ports.BookAppointmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@CrossOrigin(origins = "http://localhost:5173")
public class AppointmentController {

    private final BookAppointmentUseCase bookAppointmentUseCase;

    public  AppointmentController(BookAppointmentUseCase bookAppointmentUseCase) {
        this.bookAppointmentUseCase = bookAppointmentUseCase;
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

}
