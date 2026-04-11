package com.findmeadoc.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateAppointmentRequest(
        Long doctorId,
        LocalDate appointmentDate,
        LocalTime appointmentTime
) {
}
