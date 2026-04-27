package com.findmeadoc.application.ports;

public interface CancelAppointmentUseCase {
    void execute(Long appointmentId, String doctorEmail);
}
