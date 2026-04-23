package com.findmeadoc.domain.repositories;

import com.findmeadoc.domain.models.Appointment;
import com.findmeadoc.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Bridge to db
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByPatient(User patient);

    List<Appointment> findByPatientUserEmail(String email);

    List<Appointment> findByDoctorUserEmailOrderByAppointmentDateAscAppointmentTimeAsc(String email);

    // DB query to group and count the bookings by time, efficient than getting alll the appointments and looping
    @Query("SELECT a.appointmentTime AS startTime, COUNT(a) AS bookingCount " +
            "FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date " +
            "GROUP BY a.appointmentTime")
    List<Object[]> countBookingsByStartTime(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    // Default method to convert the database results into a Java map
    default Map<LocalTime, Integer> findBookedSlotCountByDoctorAndDate(Long doctorId, LocalDate date) {
        List<Object[]> results = countBookingsByStartTime(doctorId, date);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (LocalTime) row[0], // Extract the start time
                        row -> ((Number) row[1]).intValue() // Extract the count and convert to int
                ));
    }

}
