package com.findmeadoc.application.dto;

import java.time.LocalTime;

public record AvailableSlotResponse(
        LocalTime startTime
) {
}