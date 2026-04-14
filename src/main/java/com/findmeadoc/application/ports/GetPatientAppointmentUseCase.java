package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.AppointmentDashboardResponse;

import java.util.List;

public interface GetPatientAppointmentUseCase {
    List<AppointmentDashboardResponse> execute(String patientEmail);
}
