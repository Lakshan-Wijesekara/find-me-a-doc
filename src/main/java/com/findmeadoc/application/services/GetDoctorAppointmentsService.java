package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.DoctorAppointmentDashboardResponse;
import com.findmeadoc.application.dto.TriageResponse;
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
                    TriageResponse aiBrief = null;

                    // Check if the map is not null and not empty
                    if (appointment.getAiBrief() != null && !appointment.getAiBrief().isEmpty()) {

                        // Fetch values using the exact JSON keys
                        String urgency = (String) appointment.getAiBrief().get("urgencyLevel");
                        String viral = (String) appointment.getAiBrief().get("viralLikelihood");
                        String doctorNotes = (String) appointment.getAiBrief().get("doctorBrief"); // Ensure this key matches exactly what you saved!

                        aiBrief = new TriageResponse(
                                null,
                                null,
                                urgency,
                                null,
                                viral,
                                doctorNotes
                        );
                    }
                    // Extract the patient's full name
                    String patientName = appointment.getPatient().getUser().getFullName();

                    // Map entity to DTO
                    return new DoctorAppointmentDashboardResponse(
                            appointment.getId().toString(),
                            patientName,
                            appointment.getAppointmentDate(),
                            appointment.getAppointmentTime(),
                            appointment.getStatus(),
                            aiBrief
                    );
                })
                .collect(Collectors.toList());
    }
}
