package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.DoctorAppointmentDashboardResponse;

import java.util.List;

public interface GetDoctorAppointmentsUseCase {
    List<DoctorAppointmentDashboardResponse> execute(String doctorEmail);
}
