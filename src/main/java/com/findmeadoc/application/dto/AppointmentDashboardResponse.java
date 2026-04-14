package com.findmeadoc.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDashboardResponse(
        String bookingId,
        String doctorName,
        String specialization,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status
) {
}
