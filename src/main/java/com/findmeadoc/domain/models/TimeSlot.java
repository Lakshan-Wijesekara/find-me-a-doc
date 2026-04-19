package com.findmeadoc.domain.models;

import java.time.LocalTime;

// Using a record here without getter setter class, for immutability (don't allow changing) and less boilerplate code
public record TimeSlot(
        LocalTime startTime,
        LocalTime endTime
) {}
