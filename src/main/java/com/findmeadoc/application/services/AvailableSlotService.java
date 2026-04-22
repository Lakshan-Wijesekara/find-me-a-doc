package com.findmeadoc.application.services;

import com.findmeadoc.application.dto.AvailableSlotResponse;
import com.findmeadoc.application.ports.GetAvailableSlotsUseCase;
import com.findmeadoc.domain.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvailableSlotService implements GetAvailableSlotsUseCase {

    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AvailableSlotResponse> execute(Long doctorId, LocalDate date) {
        // Standard working hours (e.g., 9:00 AM to 5:00 PM)
        LocalTime shiftStart = LocalTime.of(9, 0);
        LocalTime shiftEnd = LocalTime.of(17, 0);

        // Fetch the map of current bookings for that day (reusing your exact repository method!)
        Map<LocalTime, Integer> dailyBookings = appointmentRepository
                .findBookedSlotCountByDoctorAndDate(doctorId, date);

        List<AvailableSlotResponse> availableSlots = new ArrayList<>();

        // Generate slots in 1-hour increments
        LocalTime currentSlot = shiftStart;
        while (currentSlot.isBefore(shiftEnd)) {

            // Check how many people have booked this specific time
            int currentBookings = dailyBookings.getOrDefault(currentSlot, 0);

            // If it hasn't hit the limit of 10, will be sent to the frontend
            if (currentBookings < 10) {
                availableSlots.add(new AvailableSlotResponse(currentSlot));
            }

            // Move to the next hour
            currentSlot = currentSlot.plusHours(1);
        }

        return availableSlots;
    }
}