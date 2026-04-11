package com.findmeadoc.domain.repositories;

import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

// Bridge to db
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByPatient(User patient);
}
