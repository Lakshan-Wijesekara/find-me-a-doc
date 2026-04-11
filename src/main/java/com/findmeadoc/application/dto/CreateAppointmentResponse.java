package com.findmeadoc.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentResponse(
        String doctorName,
        String bookingId,
        LocalDate appointmentDate,
        LocalTime appointmentTime
) {
}
