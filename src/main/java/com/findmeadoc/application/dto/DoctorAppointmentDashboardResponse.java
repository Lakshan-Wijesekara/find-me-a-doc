package com.findmeadoc.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record DoctorAppointmentDashboardResponse(
        String appointmentId,
        String patientName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status
) {
}
