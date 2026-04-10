package com.findmeadoc.application.ports;

import com.findmeadoc.application.dto.DoctorProfileResponse;
import com.findmeadoc.application.dto.DoctorUpdateRequest;

public interface UpdateDoctorProfileUseCase {
    // Need the email and update request
    DoctorProfileResponse updateDoctorProfile(String email, DoctorUpdateRequest request);
}

