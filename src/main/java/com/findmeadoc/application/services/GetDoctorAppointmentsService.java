package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorAppointmentDashboardResponse;
import com.findmeadoc.application.ports.GetDoctorAppointmentsUseCase;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GetDoctorAppointmentsService implements GetDoctorAppointmentsUseCase {
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAppointmentDashboardResponse> execute(String doctorEmail) {

        // Fetch appointments with doctor email for the doctor
        return appointmentRepository.findByDoctorUserEmailOrderByAppointmentDateAscAppointmentTimeAsc(doctorEmail)
                .stream()
                .map(appointment -> {
                    // Extract the patient's full name
                    String patientName = appointment.getPatient().getUser().getFullName();

                    // Map entity to DTO
                    return new DoctorAppointmentDashboardResponse(
                            appointment.getId().toString(),
                            patientName,
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }
}
