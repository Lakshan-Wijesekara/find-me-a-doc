package com.findmeadoc.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public record CreateAppointmentRequest(
        Long doctorId,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        Map<String, Object> aiBrief
) {
}
