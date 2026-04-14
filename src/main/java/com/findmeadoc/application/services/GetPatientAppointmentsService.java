package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.AppointmentDashboardResponse;
import com.findmeadoc.application.ports.GetPatientAppointmentUseCase;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPatientAppointmentsService implements GetPatientAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional(readOnly = true) // Since only get, improve db fetch
    public List<AppointmentDashboardResponse> execute(String patientEmail) {

        // Fetch from the database and start the stream
        return appointmentRepository.findByPatientEmail(patientEmail)
                .stream()
                .map(appointment -> {

                    String doctorName = "Dr. " + appointment.getDoctor().getUser().getFullName();
                    String specialization = appointment.getDoctor().getSpecialization(); // Fixed spelling

                    return new AppointmentDashboardResponse(
                            appointment.getId().toString(),
                            doctorName,
                            specialization,
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus()
                    );
                })
                .collect(Collectors.toList()); // Collect all to a new list
    }
}
