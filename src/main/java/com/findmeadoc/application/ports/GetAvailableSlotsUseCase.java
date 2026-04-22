package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.AvailableSlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface GetAvailableSlotsUseCase {
    List<AvailableSlotResponse> execute(Long doctorId, LocalDate date);
}