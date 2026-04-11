package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.CreateAppointmentRequest;
import com.findmeadoc.application.dto.CreateAppointmentResponse;

public interface BookAppointmentUseCase {
    /**
     * Executes the business logic to book a new appointment.
     * @param request The data from the frontend (Doctor ID, Date, Time)
     * @param patientEmail The email extracted from the JWT token
     * @return A response DTO containing the confirmation details
     */

    CreateAppointmentResponse execute(CreateAppointmentRequest request, String patientEmail);
}
