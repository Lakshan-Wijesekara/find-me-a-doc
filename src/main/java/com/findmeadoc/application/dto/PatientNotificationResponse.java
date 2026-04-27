package com.findmeadoc.application.dto;

public record PatientNotificationResponse(
        String message,
        Long appointmentId,
        String newStatus
) {
}
